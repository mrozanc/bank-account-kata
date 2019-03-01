package fr.rozanc.kata.bankaccountapi.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Invalid amount")
class InvalidAmountException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
