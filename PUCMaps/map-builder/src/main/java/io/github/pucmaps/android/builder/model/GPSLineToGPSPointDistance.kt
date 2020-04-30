package io.github.pucmaps.android.builder.model

import kotlin.math.*

data class GPSLineToGPSPointDistance(
    val line: GPSLine,
    val point: GPSPoint
) : GPSDistance {

    override val inDegrees: GPSDegree by lazy {
        val x0 = point.longitude
        val y0 = point.latitude
        val (y1, x1) = line.begin
        val (y2, x2) = line.end

        val numberer = ((y2 - y1) * x0) - ((x2 - x1) * y0) + (x2 * y1) - (y2 * x1)
        val denominator = (y2 - y1).pow(2) + (x2 - x1).pow(2)

        GPSDegree(abs(numberer.degrees) / sqrt(denominator.degrees))
    }

    override val inMeters: Double by lazy {
        getPointOnLineClosestTo(point).getDistanceTo(point).inMeters
    }

    override fun compareTo(other: GPSDistance): Int {
        return this.inDegrees.compareTo(other.inDegrees)
    }

    private fun getPointOnLineClosestTo(target: GPSPoint): GPSPoint {
        val (y0, x0) = target
        val (y1, x1) = line.begin
        val (y2, x2) = line.end

        val a = (y1 - y2)
        val b = (x2 - x1)
        val c = ((x1 * y2) - (y1 * x2))

        val x = (b * ((b * x0) - (a * y0)) - (a * c)) / (a.pow(2) + b.pow(2))
        val y = (a * ((-b * x0) + (a * y0)) - (b * c)) / (a.pow(2) + b.pow(2))

        return GPSPoint(y, x)
    }
}