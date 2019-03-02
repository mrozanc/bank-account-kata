package fr.rozanc.kata.bankaccountapi

import com.fasterxml.jackson.databind.ObjectMapper
import fr.rozanc.kata.bankaccountapi.model.BankAccountOperation
import fr.rozanc.kata.bankaccountapi.model.BankAccountStatement
import org.hamcrest.Matchers.`is`
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class BankAccountApiApplicationTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var accountService: BankAccountService

    @MockBean
    private lateinit var clock: Clock

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        (accountService as KataBankAccountService).reset()
        `when`(clock.zone).thenReturn(ZoneOffset.UTC)
        `when`(clock.instant()).thenReturn(Instant.ofEpochMilli(1551481286185))
                .thenReturn(Instant.ofEpochMilli(1551481386185))
                .thenReturn(Instant.ofEpochMilli(1551481486185))
    }

    @Test
    fun `When I create an account Then the default amount is 0`() {
        val accountNumber = accountService.createAccount().accountNumber
        assertEquals(0.00, accountService.getAccount(accountNumber).balance, 0.01)
    }

    // region Deposit
    // In order to save money
    // As a bank client
    // I want to make a deposit in my account

    @Test
    fun `Given I have an account, When I make a deposit, Then the amount is added to my account`() {
        val accountNumber = accountService.createAccount(12.00).accountNumber

        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, 121.05))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(133.05)))

        assertEquals(133.05, accountService.getAccount(accountNumber).balance, 0.01)
    }

    @Test
    fun `Given I have an account, When I make a deposit And the amount is negative, Then I get an error`() {
        val accountNumber = accountService.createAccount(12.00).accountNumber

        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, -10.00))))
                .andExpect(status().`is`(400))

        assertEquals(12.00, accountService.getAccount(accountNumber).balance, 0.01)
    }

    @Test
    fun `Given I don't have an account, When I make a deposit, I have an error`() {
        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(1, 121.05))))
                .andExpect(status().`is`(404))
//                .andExpect(jsonPath("$.message", `is`("Account not found")))
    }

    // endregion Deposit

    // region Withdrawal
    // In order to retrieve some or all of my savings
    // As a bank client
    // I want to make a withdrawal from my account
    @Test
    fun `Given I have an account, When I make a withdrawal, Then the amount is taken from my account`() {
        val accountNumber = accountService.createAccount(131.00).accountNumber

        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, 121.50))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(9.50)))

        assertEquals(9.50, accountService.getAccount(accountNumber).balance, 0.01)
    }

    @Test
    fun `Given I have an account, When I make a withdrawal and the amount is higher than the balance, Then I get an error`() {
        val accountNumber = accountService.createAccount(5.00).accountNumber

        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, 121.50))))
                .andExpect(status().`is`(400))
//                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
//                .andExpect(jsonPath("$.message", `is`("Unauthorized operation: amount exceeds account balance")))

        assertEquals(5.00, accountService.getAccount(accountNumber).balance, 0.01)
    }

    @Test
    fun `Given I have an account, When I make a withdrawal And the amount is negative Then I get an error`() {
        val accountNumber = accountService.createAccount(131.00).accountNumber

        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, -121.50))))
                .andExpect(status().`is`(400))

        assertEquals(131.00, accountService.getAccount(accountNumber).balance, 0.01)
    }

    @Test
    fun `Given I don't have an account, When I make a withdrawal, Then I get an error`() {
        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(1, 121.50))))
                .andExpect(status().`is`(404))
//                .andExpect(jsonPath("$.message", `is`("Unauthorized operation: amount exceeds account balance")))
    }

    // endregion Withdrawal

    // region Statement
    @Test
    fun `Given I have an account, When I make operations And I ask a statement, Then I can see my history`() {
        val accountNumber = accountService.createAccount(80.00).accountNumber

        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, 20.05))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(100.05)))

        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(BankAccountOperation(accountNumber, 75.00))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(25.05)))

        mvc.perform(get("/api/account/$accountNumber/statements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<List<BankAccountStatement>>(3)))
                .andExpect(jsonPath("$[0].accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$[0].date", `is`("2019-03-01T23:01:26.185")))
                .andExpect(jsonPath("$[0].amount", `is`(80.00)))
                .andExpect(jsonPath("$[0].balance", `is`(80.00)))
                .andExpect(jsonPath("$[1].accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$[1].date", `is`("2019-03-01T23:03:06.185")))
                .andExpect(jsonPath("$[1].amount", `is`(20.05)))
                .andExpect(jsonPath("$[1].balance", `is`(100.05)))
                .andExpect(jsonPath("$[2].accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$[2].date", `is`("2019-03-01T23:04:46.185")))
                .andExpect(jsonPath("$[2].amount", `is`(-75.00)))
                .andExpect(jsonPath("$[2].balance", `is`(25.05)))
    }

    @Test
    fun `Given I don't have an account, When I ask a statement, Then I get an error`() {
        mvc.perform(get("/api/account/1/statements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().`is`(404))
//                .andExpect(jsonPath("$.message", `is`("BankAccount not found")))
    }

    // endregion Statement
}
