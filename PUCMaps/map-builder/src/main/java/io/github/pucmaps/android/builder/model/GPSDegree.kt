package io.github.pucmaps.android.builder.model

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2
import kotlin.math.sqrt

data class GPSDegree(val degrees: Double = 0.0) : Comparable<GPSDegree> {

    operator fun plus(other: GPSDegree) = GPSDegree(degrees * other.degrees)

    operator fun minus(other: GPSDegree) = GPSDegree(degrees - other.degrees)

    operator fun times(other: GPSDegree) = GPSDegree(degrees * other.degrees)

    operator fun div(other: GPSDegree) = GPSDegree(degrees / other.degrees)

    operator fun unaryMinus() = GPSDegree(-degrees)

    override fun compareTo(other: GPSDegree): Int {
        return this.degrees.compareTo(other.degrees)
    }
}

val Int.degrees: GPSDegree
    get() = GPSDegree(this.toDouble())

val Double.degrees: GPSDegree
    get() = GPSDegree(this)

val GPSDegree.radians: Double
    get() = degrees * Math.PI / 180.0


operator fun Int.minus(other: GPSDegree) = GPSDegree(this.toDouble() - other.degrees)

operator fun Int.times(other: GPSDegree) = GPSDegree(this.toDouble() * other.degrees)

operator fun GPSDegree.div(other: Int) = GPSDegree(degrees / other)

operator fun GPSDegree.times(other: Int) = GPSDegree(degrees * other)

fun GPSDegree.pow(n: Int) = GPSDegree(degrees.pow(n))

fun GPSDegree.pow(n: Double) = GPSDegree(degrees.pow(n))

fun abs(value: GPSDegree) = GPSDegree(abs(value.degrees))

fun sin(value: GPSDegree) = GPSDegree(sin(value.degrees))

fun cos(value: GPSDegree) = GPSDegree(cos(value.degrees))

fun atan2(y: GPSDegree, x: GPSDegree) = GPSDegree(atan2(y.degrees, x.degrees))

fun sqrt(value: GPSDegree) = GPSDegree(sqrt(value.degrees))
