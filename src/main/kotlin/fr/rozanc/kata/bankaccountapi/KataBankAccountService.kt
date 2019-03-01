package fr.rozanc.kata.bankaccountapi

import fr.rozanc.kata.bankaccountapi.exceptions.AccountNotFoundException
import fr.rozanc.kata.bankaccountapi.exceptions.InvalidAmountException
import fr.rozanc.kata.bankaccountapi.model.BankAccount
import fr.rozanc.kata.bankaccountapi.model.BankAccountStatement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Math.round
import java.time.Clock
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

@Service
class KataBankAccountService(@Autowired private val clock: Clock) : BankAccountService {

    private val lastAccountId = AtomicInteger()
    private val accounts = Collections.synchronizedMap(HashMap<Int, BankAccount>())
    private val statements = Collections.synchronizedList(ArrayList<BankAccountStatement>())

    override fun createAccount(amount: Double): BankAccount {
        val accountNumber = lastAccountId.incrementAndGet()
        val account = BankAccount(accountNumber, amount)
        accounts[accountNumber] = account
        statements += BankAccountStatement(accountNumber, date = LocalDateTime.now(clock), amount = amount, balance = account.balance)
        return account
    }

    override fun getAccount(accountNumber: Int): BankAccount {
        return accounts[accountNumber] ?: throw AccountNotFoundException("Account $accountNumber does not exist")
    }

    override fun addAmount(accountNumber: Int, amount: Double): BankAccount {
        val account = accounts[accountNumber] ?: throw AccountNotFoundException("Account $accountNumber does not exist")
        if (amount + account.balance < 0) throw InvalidAmountException("Unauthorized operation: amount exceeds account balance")
        val newAccount = account.copy(balance = round((account.balance + amount) * 100.0) / 100.0)
        accounts[accountNumber] = newAccount
        statements += BankAccountStatement(accountNumber, date = LocalDateTime.now(clock), amount = amount, balance = newAccount.balance)
        return newAccount
    }

    override fun getHistory(accountNumber: Int): List<BankAccountStatement> {
        if (!accounts.containsKey(accountNumber)) throw AccountNotFoundException("Account $accountNumber does not exist")
        return statements.filter { it.accountNumber == accountNumber }
    }

    fun reset() {
        accounts.clear()
        statements.clear()
        lastAccountId.set(0)
    }
}
