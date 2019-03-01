package fr.rozanc.kata.bankaccountapi

import fr.rozanc.kata.bankaccountapi.exceptions.AccountNotFoundException
import fr.rozanc.kata.bankaccountapi.exceptions.InvalidAmountException
import fr.rozanc.kata.bankaccountapi.model.BankAccountStatement
import fr.rozanc.kata.bankaccountapi.model.BankAccount
import org.springframework.stereotype.Service
import java.lang.Math.round
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Service
class KataBankAccountService : BankAccountService {

    private val lastAccountId = AtomicInteger()
    private val accounts = Collections.synchronizedMap(HashMap<Int, BankAccount>())

    override fun createAccount(amount: Double): BankAccount {
        val accountNumber = lastAccountId.incrementAndGet()
        val account = BankAccount(accountNumber, amount)
        accounts[accountNumber] = account
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
        return newAccount
    }

    override fun getHistory(accountNumber: Int): List<BankAccountStatement> {
        TODO("not implemented")
    }

    fun reset() {
        accounts.clear()
        lastAccountId.set(0)
    }
}
