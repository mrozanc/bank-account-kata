package fr.rozanc.kata.bankaccountapi

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger

class PredefinedValuesClock(private val millis: List<Long>) : Clock() {

    private val systemClock: Clock = Clock.systemDefaultZone()

    private val index = AtomicInteger()

    override fun withZone(zone: ZoneId?): Clock {
        return systemClock.withZone(zone)
    }

    override fun getZone(): ZoneId {
        return systemClock.zone
    }

    override fun instant(): Instant {
        return Instant.ofEpochMilli(millis[index.getAndIncrement() % millis.size])
    }

    fun reset() {
        index.set(0)
    }
}