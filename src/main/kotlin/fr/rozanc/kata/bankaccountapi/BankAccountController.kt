package fr.rozanc.kata.bankaccountapi

import fr.rozanc.kata.bankaccountapi.exceptions.InvalidAmountException
import fr.rozanc.kata.bankaccountapi.model.BankAccountOperation
import fr.rozanc.kata.bankaccountapi.model.BankAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class BankAccountController {

    @Autowired
    private lateinit var bankAccountService: BankAccountService

    @PostMapping("/deposit")
    fun deposit(@RequestBody depositOperation: BankAccountOperation): BankAccount {
        if (depositOperation.amount <= 0) {
            throw InvalidAmountException("Invalid negative amount: ${depositOperation.amount}")
        }

        return bankAccountService.addAmount(depositOperation.accountNumber, depositOperation.amount)
    }

    @PostMapping("/withdrawal")
    fun withdrawal(@RequestBody depositOperation: BankAccountOperation): BankAccount {
        if (depositOperation.amount <= 0) {
            throw InvalidAmountException("Invalid negative amount: ${depositOperation.amount}")
        }

        return bankAccountService.addAmount(depositOperation.accountNumber, -depositOperation.amount)
    }

}