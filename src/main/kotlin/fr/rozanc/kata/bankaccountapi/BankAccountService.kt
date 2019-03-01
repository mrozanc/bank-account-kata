package fr.rozanc.kata.bankaccountapi

import fr.rozanc.kata.bankaccountapi.model.BankAccountStatement
import fr.rozanc.kata.bankaccountapi.model.BankAccount

interface BankAccountService {

    fun createAccount(amount: Double = 0.0): BankAccount

    fun getAccount(accountNumber: Int): BankAccount

    fun addAmount(accountNumber: Int, amount: Double): BankAccount

    fun getHistory(accountNumber: Int): List<BankAccountStatement>
}
