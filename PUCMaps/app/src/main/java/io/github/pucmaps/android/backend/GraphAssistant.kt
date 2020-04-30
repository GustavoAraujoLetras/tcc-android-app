package io.github.pucmaps.android.backend

import io.github.pucmaps.android.builder.model.GPSLine
import com.google.android.gms.maps.model.LatLng as MapCoordinates
import io.github.pucmaps.android.builder.model.GPSPoint
import io.github.pucmaps.android.builder.model.radians
import io.github.pucmaps.android.builder.model.toGPSPosition
import kotlin.math.abs
import kotlin.math.sqrt

class GraphAssistant(
    val vertices: List<GPSPoint>,
    val edges: List<GPSLine>
) {

    private val searchAlgorithm = AlgorithmAStarImpl(edges)
    private val maxDistanceInMeters = 1

    fun getEdgeClosestTo(coordinates: MapCoordinates): GPSLine? {
        val gpsPosition = coordinates.toGPSPosition()

        return edges
            .asSequence()
            .map { it to it.getDistanceTo(gpsPosition) }
            .sortedBy { it.second }
            .filter { it.second.inMeters <= maxDistanceInMeters }
            .firstOrNull()
            ?.first
    }

    fun getVertexClosestTo(coordinates: MapCoordinates): GPSPoint? {
        val gpsPosition = coordinates.toGPSPosition()

        return vertices
            .asSequence()
            .map { it to it.getDistanceTo(gpsPosition) }
            .sortedBy { it.second }
            .filter { it.second.inMeters <= maxDistanceInMeters }
            .firstOrNull()
            ?.first
    }

    fun findPath(from: GPSPoint, to: GPSPoint): List<GPSLine> {
        return searchAlgorithm.findPath(
            AlgorithmAStarImpl.GraphCompatibleVertex(from),
            AlgorithmAStarImpl.GraphCompatibleVertex(to)
        ).map { it.edge }
    }

}