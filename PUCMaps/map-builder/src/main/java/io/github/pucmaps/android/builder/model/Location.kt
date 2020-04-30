package io.github.pucmaps.android.builder.model

import android.location.Location as AndroidLocation
import android.os.Build
import android.os.Bundle

data class Location(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val accuracy: Float = 0f,
    val altitude: Double = 0.0,
    val bearing: Float = 0f,
    val bearingAccuracyDegrees: Float? = null,
    val elapsedRealtimeNanos: Long = 0L,
    val provider: String = "",
    val speed: Float = 0f,
    val time: Long = 0L,
    val speedAccuracyMetersPerSecond: Float? = null,
    val verticalAccuracyMeters: Float? = null,
    val extras: Bundle = Bundle()
) {
    companion object {
        operator fun invoke(androidLocation: AndroidLocation): Location {
            val bearingAccuracyDegrees: Float?
            val speedAccuracyMetersPerSecond: Float?
            val verticalAccuracyMeters: Float?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bearingAccuracyDegrees = androidLocation.bearingAccuracyDegrees
                speedAccuracyMetersPerSecond = androidLocation.speedAccuracyMetersPerSecond
                verticalAccuracyMeters = androidLocation.verticalAccuracyMeters
            } else {
                bearingAccuracyDegrees = null
                speedAccuracyMetersPerSecond = null
                verticalAccuracyMeters = null
            }

            return Location(
                longitude = androidLocation.longitude,
                latitude = androidLocation.latitude,
                accuracy = androidLocation.accuracy,
                altitude = androidLocation.altitude,
                bearing = androidLocation.bearing,
                bearingAccuracyDegrees = bearingAccuracyDegrees,
                elapsedRealtimeNanos = androidLocation.elapsedRealtimeNanos,
                provider = androidLocation.provider,
                speed = androidLocation.speed,
                time = androidLocation.time,
                speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond,
                verticalAccuracyMeters = verticalAccuracyMeters,
                extras = androidLocation.extras
            )
        }
    }
}