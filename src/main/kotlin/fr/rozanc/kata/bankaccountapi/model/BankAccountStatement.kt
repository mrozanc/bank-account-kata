package fr.rozanc.kata.bankaccountapi.model

import java.time.LocalDateTime

data class BankAccountStatement(val accountNumber: Int,
                                val date: LocalDateTime = LocalDateTime.now(),
                                val amount: Double,
                                val balance: Double)
