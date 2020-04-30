package io.github.pucmaps.android

//import com.google.android.gms.maps.model.LatLng
//import io.github.pucmaps.android.backend.GraphAssistant
//import io.github.pucmaps.android.builder.model.GPSPoint
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import kotlin.math.sqrt
//
//class GraphAssistantTest {
//
//    private lateinit var graphAssistant: GraphAssistant
//
//    private val mapLocations = listOf(
//        LatLng(0.0, 0.0),
//        LatLng(1.0, 1.0),
//        LatLng(3.0, 4.0),
//        LatLng(4.0, 6.0)
//    )
//
//    private val verticesList = listOf(
//        GPSPoint(0.0, 0.0),
//        GPSPoint(1.0, 1.0),
//        GPSPoint(1.0, 2.0),
//        GPSPoint(3.0, 4.0)
//    )
//
//    private val distanceBetweenMapLocationAndVertex = mapOf(
//        mapLocations[0] to verticesList[0] to 0.0,
//        mapLocations[1] to verticesList[0] to sqrt(2.0),
//        mapLocations[1] to verticesList[1] to 0.0,
//        mapLocations[2] to verticesList[3] to 0.0,
//        mapLocations[2] to verticesList[0] to 5.0,
//        mapLocations[3] to verticesList[2] to 5.0
//    )
//
//    private val closestVertexToMapLocation = mapOf(
//        mapLocations[0] to verticesList[0],
//        mapLocations[1] to verticesList[1],
//        mapLocations[2] to verticesList[3],
//        mapLocations[3] to verticesList[3]
//    )
//
//
//    @Before
//    fun createGraphAssistant() {
//        graphAssistant = GraphAssistant(
//            vertices = verticesList,
//            edges = emptyList()
//        )
//    }
//
//    @Test
//    fun extensionDistanceTo() {
//        with(graphAssistant) {
//            val extension = this::class.java.getDeclaredMethod(
//                "distanceTo",
//                GPSPoint::class.java,
//                LatLng::class.java
//            )
//            extension.isAccessible = true
//
//            for (mapEntry in distanceBetweenMapLocationAndVertex) {
//                val (mapCoordinates, vertex) = mapEntry.key
//                val predictedDistance = mapEntry.value
//                val resultDistance = extension.invoke(this, vertex, mapCoordinates)
//
//                Assert.assertTrue(
//                    "vertex: $vertex | mapCoordinates: $mapCoordinates | prediction: $predictedDistance | result: $resultDistance",
//                    resultDistance == predictedDistance
//                )
//            }
//        }
//    }
//
//    @Test
//    fun testGetVertexClosestTo() {
//        for (mapCoordinates in distanceBetweenMapLocationAndVertex.keys.map { it.first }) {
//            val vertex = graphAssistant.getVertexClosestTo(mapCoordinates)
//            val prediction = closestVertexToMapLocation[mapCoordinates]
//
//            Assert.assertTrue(
//                "vertex: $vertex | mapCoordinates: $mapCoordinates | prediction: ${closestVertexToMapLocation[mapCoordinates]} | result: $vertex",
//                prediction == vertex
//            )
//        }
//    }
//
//}