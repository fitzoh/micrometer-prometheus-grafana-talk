package com.github.fitzoh.monitoring

import io.micrometer.core.instrument.MeterRegistry
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


data class ChaosEvent(val start: Instant = Instant.EPOCH, val end: Instant = Instant.EPOCH) {
    fun isActive(at: Instant) = start.isBefore(at) && end.isAfter(at)

    fun isExpired(at: Instant) = start.isBefore(at) && end.isBefore(at)
}

private fun Random.nextInt(lower: Int, upper: Int) = lower + this.nextInt(upper - lower)


class ChaosState {

    private val event = AtomicReference(ChaosEvent())

    fun isEventActive(): Boolean {
        val now = Instant.now()
        val event = event.get()
        if (event.isExpired(now)) {
            scheduleNextEvent(now)
        }
        return event.isActive(now)
    }

    private fun scheduleNextEvent(now: Instant) {
        val secondsUntilNextEvent = random.nextInt(10, 45)
        val start = now.plusSeconds(secondsUntilNextEvent.toLong())
        val duration = random.nextInt(5, 15)
        val end = start.plusSeconds(duration.toLong())
        event.set(ChaosEvent(start, end))
        log.info("next chaos event scheduled: {} {}", StructuredArguments.keyValue("startsIn", secondsUntilNextEvent), StructuredArguments.keyValue("duration", duration))
    }

    companion object {
        val random = Random()
        val log: Logger = LoggerFactory.getLogger(ChaosState::class.java)
    }
}

@Component
class LatencyGenerator {
    private val chaos = ChaosState()
    fun next(): Int {
        val roll = random.nextFloat()

        return if (chaos.isEventActive()) {
            when {
                roll < .5 -> fast()
                roll < .9 -> medium()
                else -> slow()
            }
        } else {
            when {
                roll < .1 -> fast()
                roll < .5 -> medium()
                roll < .9 -> slow()
                else -> reallySlow()
            }
        }
    }

    private fun fast() = random.nextInt(maxMillisFast)
    private fun medium() = random.nextInt(maxMillisFast, maxMillisMedium)
    private fun slow() = random.nextInt(maxMillisMedium, maxMillisSlow)
    private fun reallySlow() = random.nextInt(maxMillisSlow, maxMillisReallySlow)

    companion object {
        const val maxMillisFast = 20
        const val maxMillisMedium = 100
        const val maxMillisSlow = 500
        const val maxMillisReallySlow = 10_000
        val random = Random()
    }
}

@Component
class HttpStatusGenerator {
    private val chaos = ChaosState()
    fun next(): HttpStatus {
        val roll = random.nextFloat()

        return if (chaos.isEventActive()) {
            when {
                roll < .1 -> HttpStatus.BAD_REQUEST
                roll < .6 -> HttpStatus.INTERNAL_SERVER_ERROR
                else -> HttpStatus.OK
            }
        } else {
            when {
                roll < .1 -> HttpStatus.BAD_REQUEST
                roll < .2 -> HttpStatus.INTERNAL_SERVER_ERROR
                else -> HttpStatus.OK
            }
        }
    }

    companion object {
        val random = Random()
    }
}

@RestController
class RandomEndpoint(
        val latencyGenerator: LatencyGenerator,
        val httpStatusGenerator: HttpStatusGenerator,
        meterRegistry: MeterRegistry) {
    val randomWalk = AtomicLong()
    val random = Random()

    init {
        meterRegistry.gauge("my.random.walk", randomWalk)
    }

    @GetMapping("/random")
    fun endpoint(): Mono<ResponseEntity<String>> {
        if (random.nextBoolean()) randomWalk.incrementAndGet() else randomWalk.decrementAndGet()

        val status = httpStatusGenerator.next()
        val latency = latencyGenerator.next()

        val response = ResponseEntity.status(status).body("status: $status\n latency: $latency ms")
        return Mono.just(response).delayElement(Duration.ofMillis(latency.toLong()))
    }
}

