package com.github.fitzoh.monitoring

import io.micrometer.core.instrument.Metrics
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock


/**
 * Every so often we should have some bad things happen.
 * This class schedules Chaos in an orderly fashion.
 * There are multiple instances (one for latency, one for http statuses), hence the `type`.
 *
 * Chaos states are also timed by a [io.micrometer.core.instrument.LongTaskTimer].
 */
class ChaosState(val type: String) {


    data class ChaosEvent(val start: Instant = Instant.EPOCH, val end: Instant = Instant.EPOCH) {
        fun isActive(at: Instant) = start.isBefore(at) && end.isAfter(at)

        fun isExpired(at: Instant) = start.isBefore(at) && end.isBefore(at)
    }


    private val event = AtomicReference(ChaosEvent())
    private val lock = ReentrantLock()
    private val timer = Metrics.globalRegistry.more().longTaskTimer("chaos", "type", type)

    fun isEventActive(): Boolean {
        val now = Instant.now()
        val event = event.get()
        if (event.isExpired(now)) {
            scheduleNextEvent(now)
        }
        return event.isActive(now)
    }

    private fun scheduleNextEvent(now: Instant) {
        if (lock.tryLock()) {
            val secondsUntilNextEvent = random.nextInt(60, 120)
            val start = now.plusSeconds(secondsUntilNextEvent.toLong())
            val duration = random.nextInt(20, 40)
            val end = start.plusSeconds(duration.toLong())
            event.set(ChaosEvent(start, end))
            Mono.delay(Duration.ofSeconds(secondsUntilNextEvent.toLong()))
                    .map {
                        log.info("{} chaos event started", type)
                        timer.start()
                    }
                    .delayElement(Duration.ofSeconds(duration.toLong()))
                    .subscribe { sample ->
                        log.info("{} chaos event ended", type)
                        sample.stop()
                    }
        }
    }

    companion object {
        val random = Random()
        val log: Logger = LoggerFactory.getLogger(ChaosState::class.java)
    }
}
