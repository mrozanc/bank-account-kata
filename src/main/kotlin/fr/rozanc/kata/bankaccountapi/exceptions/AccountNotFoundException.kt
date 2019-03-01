package fr.rozanc.kata.bankaccountapi.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Account not found")
class AccountNotFoundException(message: String, cause: Throwable? = null): RuntimeException(message, cause)
