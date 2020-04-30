package io.github.pucmaps.android.backend

import android.util.Log

abstract class AlgorithmAStar<V : Graph.Vertex, E : Graph.Edge<V>>(
    protected val edges: List<E>
) : Graph {
    private val V.neighbors: List<V>
        get() = edges
            .asSequence()
            .filter { it.a == this || it.b == this }
            .map { listOf(it.a, it.b) }
            .flatten()
            .filterNot { it == this }
            .distinct()
            .toList()

    private val E.cost: Double
        get() = costToMoveThrough(this)

    private fun findRoute(from: V, to: V): E? {
        return edges.find {
            (it.a == from && it.b == to) || (it.a == to && it.b == from)
        }
    }

    private fun findRouteOrElseCreateIt(from: V, to: V): E {
        return findRoute(from, to) ?: createEdge(from, to)
    }

    private fun generatePath(currentPos: V, cameFrom: Map<V, V>): List<E> {
        val path = mutableListOf<E>()
        var current = currentPos

        while (cameFrom.containsKey(current)) {
            Log.d("GOMA", "next | current: $current")
            val next = cameFrom.getValue(current)
            Log.d("GOMA", "route")
            val route = findRoute(current, next)!!


            Log.d("GOMA", "path.add")
            current = next
            path.add(0, route)
            Log.d("GOMA", "path.added")
        }

        Log.d("GOMA", "while ended")
        return path
    }

    abstract fun costToMoveThrough(edge: E): Double
    abstract fun createEdge(from: V, to: V): E

    fun findPath(begin: V, end: V): List<E> {
        val cameFrom = mutableMapOf<V, V>()

        val openVertices = mutableSetOf(begin)
        val closedVertices = mutableSetOf<V>()

        val costFromStart = mutableMapOf(begin to 0.0)

        val estimatedRoute = findRouteOrElseCreateIt(from = begin, to = end)
        val estimatedTotalCost = mutableMapOf(begin to estimatedRoute.cost)

        while (openVertices.isNotEmpty()) {
            val currentPos = openVertices.minBy { estimatedTotalCost.getValue(it) }!!

            // Check if we have reached the finish
            if (currentPos == end) {
                // Backtrack to generate the most efficient path
                // First Route to finish will be optimum route
                return generatePath(currentPos, cameFrom)
            }

            // Mark the current vertex as closed
            openVertices.remove(currentPos)
            closedVertices.add(currentPos)

            (currentPos.neighbors - closedVertices).forEach { neighbour ->
                val routeCost = findRouteOrElseCreateIt(from = currentPos, to = neighbour).cost
                val cost: Double = costFromStart.getValue(currentPos) + routeCost

                if (cost < (costFromStart[neighbour] ?: Double.MAX_VALUE)) {
                    if (!openVertices.contains(neighbour)) {
                        openVertices.add(neighbour)
                    }

                    cameFrom[neighbour] = currentPos
                    costFromStart[neighbour] = cost

                    val estimatedRemainingRouteCost = findRouteOrElseCreateIt(from = neighbour, to = end).cost
                    estimatedTotalCost[neighbour] = cost + estimatedRemainingRouteCost
                }
            }
        }

        throw IllegalArgumentException("No Path from Start $begin to Finish $end")
    }
}