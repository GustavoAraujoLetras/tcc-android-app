package io.github.pucmaps.android.builder.model

import kotlin.math.*

data class GPSPointToGPSPointDistance(
    val pointA: GPSPoint,
    val pointB: GPSPoint
) : GPSDistance {

    override val inDegrees: GPSDegree by lazy {
        val dLatitude = abs(pointA.latitude - pointB.latitude)
        val dLongitude = abs(pointA.longitude - pointB.longitude)
        sqrt(dLatitude.pow(2) + dLongitude.pow(2))
    }

    /**
     * Distance in meters. Calculations based on the following articles:
     * * [Earth radius - Wikipédia](https://en.wikipedia.org/wiki/Earth_radius)
     * * [Calculate distance, bearing and more between Latitude/Longitude points - Movable Type Script](http://www.movable-type.co.uk/scripts/latlong.html)
     */
    override val inMeters: Double by lazy {
        val earthRadius = 6_371_000 // in meters

        val dLat = abs(pointB.latitude - pointA.latitude)
        val dLon = abs(pointB.latitude - pointA.latitude)

        val lat1 = pointA.latitude
        val lat2 = pointB.latitude

        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(lat1) * cos(lat2)
        val c = atan2(sqrt(a), sqrt(1 - a)) * 2

        (earthRadius * c).radians
    }

    override fun compareTo(other: GPSDistance): Int {
        return this.inDegrees.compareTo(other.inDegrees)
    }
}