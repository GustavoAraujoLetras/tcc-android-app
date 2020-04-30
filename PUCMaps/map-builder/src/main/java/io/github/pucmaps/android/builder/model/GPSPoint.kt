package io.github.pucmaps.android.builder.model

import com.google.android.gms.maps.model.LatLng as MapPosition

data class GPSPoint(
    val latitude: GPSDegree = 0.degrees,
    val longitude: GPSDegree = 0.degrees
) {
    constructor(mapPosition: MapPosition) : this(
        latitude = GPSDegree(mapPosition.latitude),
        longitude = GPSDegree(mapPosition.longitude)
    )

    fun getDistanceTo(other: GPSPoint): GPSDistance {
        return GPSPointToGPSPointDistance(this, other)
    }
}

fun MapPosition.toGPSPosition() = GPSPoint(this)