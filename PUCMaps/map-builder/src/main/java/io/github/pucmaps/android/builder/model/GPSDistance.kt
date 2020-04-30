package io.github.pucmaps.android.builder.model

import kotlin.math.*

interface GPSDistance : Comparable<GPSDistance> {
    val inDegrees: GPSDegree
    val inMeters: Double
}