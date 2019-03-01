package fr.rozanc.kata.bankaccountapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankAccountApiApplication

fun main(args: Array<String>) {
    runApplication<BankAccountApiApplication>(*args)
}
