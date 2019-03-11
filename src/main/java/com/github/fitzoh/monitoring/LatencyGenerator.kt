package com.github.fitzoh.monitoring

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


/**
 * Determines how long HTTP requests should take to return.
 * Will take longer during a Chaos state.
 */
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
