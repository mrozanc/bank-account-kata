package fr.rozanc.kata.bankaccountapi

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Clock

@Configuration
class BankAccountConfiguration {

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}
