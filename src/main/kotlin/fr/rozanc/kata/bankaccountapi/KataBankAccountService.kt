package fr.rozanc.kata.bankaccountapi

import fr.rozanc.kata.bankaccountapi.model.AccountStatement
import fr.rozanc.kata.bankaccountapi.model.BankAccount
import org.springframework.stereotype.Service

@Service
class KataBankAccountService : BankAccountService {
    override fun createAccount(amount: Double): BankAccount {
        TODO("not implemented")
    }

    override fun getAccount(accountNumber: Int): BankAccount {
        TODO("not implemented")
    }

    override fun addAmount(accountNumber: Int, amount: Double): BankAccount {
        TODO("not implemented")
    }

    override fun getHistory(accountNumber: Int): List<AccountStatement> {
        TODO("not implemented")
    }
}
