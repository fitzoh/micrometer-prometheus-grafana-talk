package com.github.fitzoh.monitoring;

import java.util.*

fun Random.nextInt(lower: Int, upper: Int) = lower + this.nextInt(upper - lower)
