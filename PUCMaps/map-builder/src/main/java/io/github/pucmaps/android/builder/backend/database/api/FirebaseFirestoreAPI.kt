package io.github.pucmaps.android.builder.backend.database.api

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import io.github.pucmaps.android.builder.model.GPSLine
import io.github.pucmaps.android.builder.model.Place
import io.github.pucmaps.android.builder.model.GPSPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FirebaseFirestoreAPI {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val tag = FirebaseFirestoreAPI::class.java.simpleName

    object FirebaseCollections {
        val locations by lazy { db.collection("locations") }
        val places by lazy { db.collection("places") }
        val edges by lazy { db.collection("edges") }
        val vertices by lazy { db.collection("vertexes") }
    }

    suspend fun getVertexList(): List<GPSPoint> {
        return try {
            FirebaseCollections.vertices
                .get()
                .asSuspendableCoroutine()
                .documents
                .asListOfNotNull()
        } catch (e: Exception) {
            Log.w(tag, e.localizedMessage)
            emptyList()
        }
    }

    suspend fun getEdgeList(): List<GPSLine> {
        return try {
            FirebaseCollections.edges
                .get()
                .asSuspendableCoroutine()
                .documents
                .asListOfNotNull()
        } catch (e: Exception) {
            Log.w(tag, e.localizedMessage)
            emptyList()
        }
    }

    private fun toGPSPointListString(list: MutableList<DocumentSnapshot>) {
        val string = list.joinToString(
            prefix = "listOf(\n",
            postfix = ")",
            separator = ",\n"
        ) { doc ->
            val lat = doc["latitude"] as Double
            val lon = doc["longitude"] as Double

            "GPSPoint(GPSDegree($lat), GPSDegree($lon))"
        }

        Log.d("GOMA", string)
    }

    suspend fun getPlaceList(): List<Place> {
        return try {
            FirebaseCollections.places
                .get()
                .asSuspendableCoroutine()
                .documents
                .asListOfNotNull()
        } catch (e: Exception) {
            Log.w(tag, e.localizedMessage)
            emptyList()
        }
    }


    suspend fun savePlace(place: Place) {
        retry {
            FirebaseCollections.places
                .add(place)
                .asSuspendableCoroutine()
        }
    }

    suspend fun savePlaceList(placeList: List<Place>) {
        val items = placeList.map { it.toMap() }

        FirebaseCollections.places
            .addMultiple(items)
    }

    suspend fun saveEdge(edge: GPSLine) {
        retry {
            FirebaseCollections.edges
                .add(edge)
                .asSuspendableCoroutine()
        }
    }

    suspend fun saveEdgeList(edgeList: List<GPSLine>) {
        val items = edgeList.map { it.toMap() }
        FirebaseCollections.edges
            .addMultiple(items)
    }

    suspend fun saveVertex(vertex: GPSPoint) {
        retry {
            FirebaseCollections.vertices
                .add(vertex)
                .asSuspendableCoroutine()
        }
    }

    suspend fun saveVertexList(vertexList: List<GPSPoint>) {
        val items = vertexList.map { it.toMap() }
        FirebaseCollections.vertices
            .addMultiple(items)
    }


    suspend fun deleteEdgeList() {
        retry {
            FirebaseCollections.edges.delete()
        }
    }

    suspend fun deleteVertexList() {
        retry {
            FirebaseCollections.vertices.delete()
        }
    }

    // region Extensions

    @Throws
    private suspend fun CollectionReference.delete() {
        val documents = get().asSuspendableCoroutine().documents

        return with(firestore.batch()) {
            documents.forEach { delete(it.reference) }
            commit().asSuspendableCoroutine()
        }
    }

    @Throws
    private suspend fun CollectionReference.addMultiple(list: List<Map<String, Any>>) {
        with(firestore.batch()) {
            list.forEach { update(document(), it) }
            commit().asSuspendableCoroutine()
        }
    }

    @Throws
    private suspend fun <T> Task<T>.asSuspendableCoroutine(): T {
        return suspendCancellableCoroutine<T> { continuation ->
            this
                .addOnSuccessListener {
                    if (!continuation.isCancelled) {
                        continuation.resume(it)
                    }
                }
                .addOnCanceledListener {
                    if (!continuation.isCancelled) {
                        continuation.cancel(null)
                    }
                }
                .addOnFailureListener {
                    if (!continuation.isCancelled) {
                        continuation.resumeWithException(it)
                    }
                }
        }
    }

    private inline fun <reified T : Any> Collection<DocumentSnapshot>.asListOf(): List<T?> {
        return map { it.toObject(T::class.java) }
    }

    private inline fun <reified T : Any> Collection<DocumentSnapshot>.asListOfNotNull(): List<T> {
        return mapNotNull { it.toObject(T::class.java) }
    }

    private fun GPSLine.toMap() = mapOf(
        "vertexA" to begin.toMap(),
        "vertexB" to end.toMap(),
        "isDisabledPeopleAccessible" to isDisabledPeopleAccessible
    )

    private fun Place.toMap() = mapOf(
        "name" to name,
        "gpsPoint" to gpsPoint
    )

    private fun GPSPoint.toMap() = mapOf(
        "latitude" to latitude,
        "longitude" to longitude
    )

    // endregion

    // region Kotlin DLSs

    @Throws
    private suspend fun <T> retry(block: suspend () -> T): T {
//        var curDelay = 1000L // start with 1 sec
//        var attempts = 0
//        val maxAttempts = 10
//
//        while (true) {
//            attempts++
//
//            try {
        return block()
//            } catch (e: Exception) {
//                if (attempts < maxAttempts) {
//                    e.printStackTrace() // log the error
//                } else {
//                    throw e
//                }
//            }
//
//            delay(curDelay)
//            curDelay = (curDelay * 2).coerceAtMost(60000L)
//        }
    }

    // endregion
}