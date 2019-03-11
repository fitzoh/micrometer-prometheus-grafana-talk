package com.github.fitzoh.monitoring

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*


/**
 * Determines the HTTP status to return.
 * There are going to be more errors while in a Chaos state.
 */
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
