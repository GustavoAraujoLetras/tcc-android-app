package io.github.pucmaps.android.frontend.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.ViewCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import io.github.pucmaps.android.backend.GraphAssistant
import io.github.pucmaps.android.R
import io.github.pucmaps.android.builder.backend.database.api.FirebaseFirestoreAPI
import io.github.pucmaps.android.builder.helper.MapContainer
import io.github.pucmaps.android.builder.helper.PathEditorHelper
import io.github.pucmaps.android.builder.model.GPSLine
import io.github.pucmaps.android.builder.model.Place
import io.github.pucmaps.android.builder.model.GPSPoint
import io.github.pucmaps.android.maps.builder.helper.PathEditorSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng as MapCoordinates

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val onMapClickListener = GoogleMap.OnMapClickListener {
        requireNotNull(it) { "Clicked on map, but got null latlng" }

        if (begin == null) {
            begin = graphAssistant?.getVertexClosestTo(it)
            from_coordinates.setText("${begin?.latitude?.degrees}, ${begin?.longitude?.degrees}")
        } else {
            end = graphAssistant?.getVertexClosestTo(it)
            to_coordinates.setText("${end?.latitude?.degrees}, ${end?.longitude?.degrees}")
            drawShortestPath()
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private var mapContainer: MapContainer? = null
    private var graphAssistant: GraphAssistant? = null
    private var pathEditor: PathEditorHelper? = null

    private var begin: GPSPoint? = null
    private var end: GPSPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Initializes map fragment
        supportFragmentManager
            .findFragmentById(R.id.map)
            .let { it as SupportMapFragment }
            .getMapAsync(this)

        if (mapContainer != null) {
            applyWindowInsets()
        }
    }

    override fun onDestroy() {
        mapContainer?.removeOnMapClickListener(onMapClickListener)
        super.onDestroy()
    }

    @SuppressWarnings("ResourceType")
    override fun onMapReady(googleMap: GoogleMap) {
//        val pucLatLng = MapCoordinates(-19.920972, -43.991340)
        val pucLatLng = MapCoordinates(-19.923916, -43.994559)
        val zoom = 18f // 15.5f

        mapContainer = MapContainer(googleMap).apply {
            isMapClickEnabled = true

            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            map.isMyLocationEnabled = false

            applyWindowInsets()

            CameraPosition
                .fromLatLngZoom(pucLatLng, zoom)
                .let { CameraUpdateFactory.newCameraPosition(it) }
                .let { map.moveCamera(it) }

            addOnMapClickListener(onMapClickListener)
        }

        ioScope.launch {
            val vertexListAsync = async { getVertexes() }
            val edgeListAsync = async { getEdges() }

            val vertexList = vertexListAsync.await()
            val edgeList = edgeListAsync.await()

            launch(Dispatchers.Main) {
                pathEditor = PathEditorHelper(
                    settings = PathEditorSettings(),
                    vertexList = vertexList,
                    edgeList = edgeList,
                    drawLineLambda = this@MainActivity::drawLine
                )

                graphAssistant = GraphAssistant(vertexList, edgeList)
            }
        }
    }


    // region Firebase Firestore

    private suspend fun getEdges(): List<GPSLine> {
        return FirebaseFirestoreAPI.getEdgeList()
    }

    private suspend fun getPlaces(): List<Place> {
        return FirebaseFirestoreAPI.getPlaceList()
    }

    private suspend fun getVertexes(): List<GPSPoint> {
        return FirebaseFirestoreAPI.getVertexList()
    }

    // endregion


    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(top_guideline) { _, insets ->
            top_guideline.setGuidelineBegin(insets.systemWindowInsets.top)
            start_guideline.setGuidelineBegin(insets.systemWindowInsets.left)
            end_guideline.setGuidelineEnd(insets.systemWindowInsets.right)
            bottom_guideline.setGuidelineEnd(insets.systemWindowInsets.bottom)

            val mapTopGuidelineLayoutParams = (map_top_guideline.layoutParams as ConstraintLayout.LayoutParams)

            val mapTopInset = mapTopGuidelineLayoutParams.guideBegin + insets.systemWindowInsets.top

            mapContainer?.map?.setPadding(
                insets.systemWindowInsets.left,
                mapTopInset,
                insets.systemWindowInsets.right,
                insets.systemWindowInsets.bottom
            )

            insets
        }
    }

    private fun drawShortestPath() {
        val edgeList = graphAssistant?.findPath(from = begin!!, to = end!!) ?: return

        begin = null
        end = null

        pathEditor?.setEdgesHighlighted(false, pathEditor?.edges ?: emptyList())
        pathEditor?.setEdgesHighlighted(true, edgeList)
    }

    private fun showMapsActivity() {
        packageManager
            .getLaunchIntentForPackage("io.github.pucmaps.manager.android")
            ?.let { startActivity(it) }
    }


    // region Drawing

    private fun drawLine(from: MapCoordinates, to: MapCoordinates): Polyline? {
        return mapContainer?.map?.drawLine(from, to)
    }

    // endregion


    // region Extensions

    private fun GoogleMap.drawLine(from: MapCoordinates, to: MapCoordinates): Polyline {
        return PolylineOptions()
            .color(MAP_LINE_COLOR)
            .width(MAP_LINE_WIDTH)
            .add(from, to)
            .let { addPolyline(it) }
    }

    // endregion


    companion object {
        private const val RC_PERMISSIONS = 9001

        private const val MAP_LINE_COLOR = Color.BLUE
        private const val MAP_LINE_WIDTH = 5f

        private val TAG = MainActivity::class.java.simpleName
    }

}