package io.github.pucmaps.android.backend

interface Graph {
    interface Vertex
    interface Edge<T : Vertex> {
        val a: T
        val b: T
    }
}