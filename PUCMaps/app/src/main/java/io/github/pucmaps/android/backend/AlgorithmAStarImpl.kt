package io.github.pucmaps.android.backend

import io.github.pucmaps.android.builder.model.GPSLine
import io.github.pucmaps.android.builder.model.GPSPoint

class AlgorithmAStarImpl(val edgeList: List<GPSLine>) : AlgorithmAStar<
    AlgorithmAStarImpl.GraphCompatibleVertex,
    AlgorithmAStarImpl.GraphCompatibleEdge
>(edgeList.map { GraphCompatibleEdge(it) }) {

    class GraphCompatibleVertex(val vertex: GPSPoint) : Graph.Vertex {
        override fun equals(other: Any?): Boolean {
            if (other !is GraphCompatibleVertex) {
                return false
            }

            return vertex == other.vertex
        }

        override fun toString(): String {
            return vertex.toString()
        }
    }

    class GraphCompatibleEdge(val edge: GPSLine) : Graph.Edge<GraphCompatibleVertex> {
        override val a: GraphCompatibleVertex = GraphCompatibleVertex(edge.begin)
        override val b: GraphCompatibleVertex = GraphCompatibleVertex(edge.end)
    }

    override fun costToMoveThrough(edge: GraphCompatibleEdge): Double {
        return edge.cost
    }

    override fun createEdge(
        from: GraphCompatibleVertex,
        to: GraphCompatibleVertex
    ) = GraphCompatibleEdge(GPSLine(from.vertex, to.vertex))

    private val GraphCompatibleEdge.cost: Double
        get() = edge.begin.getDistanceTo(edge.end).inMeters
}