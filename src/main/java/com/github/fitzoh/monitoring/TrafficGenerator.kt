package com.github.fitzoh.monitoring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*


/**
 * This demo will be super boring if there's no traffic, so let's generate some.
 * Hits the app's different endpoints at a weighted frequency.
 */
@Component
class TrafficGenerator(
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

    /**
     * I clearly don't know how to do proper error handling with Reactor.
     */
    override fun onApplicationEvent(event: ApplicationReadyEvent) {

        val webClient = builder.build()

        Flux.interval(Duration.ofMillis(clientDelay))
                .flatMap { webClient.get().uri(url()).exchange() }
                .flatMap { it.bodyToFlux(String::class.java) }
                .subscribe()

    }
}
