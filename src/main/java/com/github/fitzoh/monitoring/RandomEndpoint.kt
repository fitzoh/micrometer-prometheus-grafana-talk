package com.github.fitzoh.monitoring

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock


data class ChaosEvent(val start: Instant = Instant.EPOCH, val end: Instant = Instant.EPOCH) {
    fun isActive(at: Instant) = start.isBefore(at) && end.isAfter(at)

    fun isExpired(at: Instant) = start.isBefore(at) && end.isBefore(at)
}

private fun Random.nextInt(lower: Int, upper: Int) = lower + this.nextInt(upper - lower)


class ChaosState(val type: String) {

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

@Component
class LatencyGenerator(
        @Value("\${demo.base-latency}") val baseLatency: Int
) {
    private val chaos = ChaosState("latency")
    fun next(): Int {
        val roll = random.nextFloat()

        val calculatedLatency = if (chaos.isEventActive()) {
            (200 + 1.5 * when {
                roll < .1 -> fast()
                roll < .5 -> medium()
                roll < .9 -> slow()
                else -> reallySlow()
            }).toInt()
        } else {
            when {
                roll < .5 -> fast()
                roll < .9 -> medium()
                else -> slow()
            }
        }

        return baseLatency + calculatedLatency
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
    private val chaos = ChaosState("http-status")
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

    @GetMapping(value = ["/random", "/also-random", "/yet-another/random"])
    fun endpoint(): Mono<ResponseEntity<String>> {
        if (random.nextBoolean()) randomWalk.incrementAndGet() else randomWalk.decrementAndGet()

        val status = httpStatusGenerator.next()
        val latency = latencyGenerator.next()

        val response = ResponseEntity.status(status).body("status: $status\n latency: $latency ms")
        return Mono.just(response)
                .delayElement(Duration.ofMillis(latency.toLong()))
                .doOnError { e -> println(e) }
    }
}

@Component
class Thingy(
        val builder: WebClient.Builder,
        @Value("\${demo.client-delay}") val clientDelay: Long,
        @Value("\${server.port}") val port: String
) : ApplicationListener<ApplicationReadyEvent> {

    val random = Random()

    fun url(): String {
        val roll = random.nextFloat()

        return when {
            roll < .5 -> "http://localhost:$port/random"
            roll < .85 -> "http://localhost:$port/also-random"
            else -> "http://localhost:$port/yet-another/random"
        }
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {

        val webClient = builder.build()
        Flux.interval(Duration.ofMillis(clientDelay))
                .flatMap { webClient.get().uri(url()).exchange() }
                .subscribe()
    }
}

