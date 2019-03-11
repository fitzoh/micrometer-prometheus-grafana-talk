package com.github.fitzoh.monitoring

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong


/**
 * Endpoint (3 actually) that returns a randomly generated HTTP status in a randomly generated amount of time
 * Also manages a metered random walk through a [io.micrometer.core.instrument.Gauge] instrumented [AtomicLong].
 */
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

