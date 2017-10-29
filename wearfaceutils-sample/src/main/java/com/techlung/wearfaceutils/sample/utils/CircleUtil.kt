package com.techlung.wearfaceutils.sample.utils

object CircleUtil {

    val SECONDS_IN_12_HOURS = 43200
    val SECONDS_IN_1_HOUR = 3600

    fun secondsToClockRadians(seconds: Int): Double {
        return radiansToClockRadians(seconds.toDouble() % SECONDS_IN_12_HOURS.toDouble() / SECONDS_IN_12_HOURS * (2 * Math.PI))
    }

    fun radiansToClockRadians(radians: Double): Double {
        return radians - Math.PI / 2
    }
}