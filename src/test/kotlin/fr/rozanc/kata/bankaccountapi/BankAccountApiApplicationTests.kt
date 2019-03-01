package fr.rozanc.kata.bankaccountapi

import com.fasterxml.jackson.databind.ObjectMapper
import fr.rozanc.kata.bankaccountapi.model.AccountOperation
import fr.rozanc.kata.bankaccountapi.model.AccountStatement
import org.hamcrest.Matchers.`is`
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class BankAccountApiApplicationTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var accountService: BankAccountService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // region Deposit
    // In order to save money
    // As a bank client
    // I want to make a deposit in my account

    @Test
    fun `Given I have an account, When I make a deposit, Then the amount is added to my account`() {
        val accountNumber = accountService.createAccount(12.00).accountNumber

        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AccountOperation(accountNumber, 121.05))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(133.05)))

        assertEquals(133.05, accountService.getAccount(accountNumber))
    }

    @Test
    fun `Given I don't have an account, When I make a deposit, I have an error`() {
        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AccountOperation(1, 121.05))))
                .andExpect(status().is4xxClientError)
                .andExpect(jsonPath("$.accountNumber", `is`(1)))
                .andExpect(jsonPath("$.message", `is`("Account not found")))
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
                .content(objectMapper.writeValueAsString(AccountOperation(accountNumber, 121.50))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(9.50)))
    }

    @Test
    fun `Given I have an account, When I make a withdrawal and the amount is higher than the balance, Then I get an error`() {
        val accountNumber = accountService.createAccount(5.00).accountNumber

        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AccountOperation(accountNumber, 121.50))))
                .andExpect(status().is4xxClientError)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.message", `is`("Unauthorized operation: amount exceeds account balance")))
    }

    // endregion Withdrawal

    // region Statement
    @Test
    fun `Given I have an account, When I make operations And I ask a statement, Then I can see my history`() {
        val accountNumber = accountService.createAccount(80.00).accountNumber

        mvc.perform(post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AccountOperation(accountNumber, 20.05))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(100.05)))

        mvc.perform(post("/api/account/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AccountOperation(accountNumber, 75.00))))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(25.05)))

        mvc.perform(get("/api/account/$accountNumber/statements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<List<AccountStatement>>(1)))
                .andExpect(jsonPath("$.accountNumber", `is`(accountNumber)))
                .andExpect(jsonPath("$.balance", `is`(25.05)))
    }

    @Test
    fun `Given I don't have an account, When I ask a statement, Then I get an error`() {
        mvc.perform(get("/api/account/1/statements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError)
                .andExpect(jsonPath("$.message", `is`("Account not found")))
    }

    // endregion Statement
}
