package io.github.pucmaps.android.builder.model

import com.google.android.gms.maps.model.LatLng

data class GPSLine(
    val begin: GPSPoint = GPSPoint(),
    val end: GPSPoint = GPSPoint(),
    val isDisabledPeopleAccessible: Boolean = true
) {
    constructor(
        latLngA: LatLng,
        latlngB: LatLng,
        isDisabledPeopleAccessible: Boolean = true
    ) : this (GPSPoint(latLngA), GPSPoint(latlngB), isDisabledPeopleAccessible)

    fun getDistanceTo(point: GPSPoint): GPSDistance {
        return GPSLineToGPSPointDistance(this, point)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GPSLine) {
            return false
        }

        if (begin == other.begin && end == other.end) {
            return true
        }

        if (begin == other.end && end == other.begin) {
            return true
        }

        return false
    }
}