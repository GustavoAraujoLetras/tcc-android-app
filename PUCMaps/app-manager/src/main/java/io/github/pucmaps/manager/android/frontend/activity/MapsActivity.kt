package io.github.pucmaps.manager.android.frontend.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.github.pucmaps.android.builder.backend.database.api.FirebaseFirestoreAPI
import io.github.pucmaps.android.builder.frontend.activity.PUCMapsActivity
import io.github.pucmaps.android.builder.helper.MapContainer
import io.github.pucmaps.android.builder.helper.PathEditorHelper
import io.github.pucmaps.android.builder.model.GPSLine
import io.github.pucmaps.android.builder.model.GPSPoint
import io.github.pucmaps.android.builder.model.Place
import io.github.pucmaps.manager.android.frontend.fragment.bottomsheet.PathEditorSettingsBottomSheet
import io.github.pucmaps.android.maps.builder.helper.PathEditorSettings
import io.github.pucmaps.android.maps.builder.helper.PathEditorShape
import io.github.pucmaps.manager.android.R
import io.github.pucmaps.manager.android.frontend.fragment.bottomsheet.VertexDetailsBottomSheet
import io.github.pucmaps.manager.android.frontend.fragment.dialog.ProgressDialog
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.CameraPosition as MapCameraPosition
import com.google.android.gms.maps.model.Circle as MapCircle
import com.google.android.gms.maps.model.CircleOptions as MapCircleBuilder
import com.google.android.gms.maps.model.LatLng as MapCoordinates
import com.google.android.gms.maps.model.Marker as MapMarker
import com.google.android.gms.maps.model.MarkerOptions as MapMarkerBuilder
import com.google.android.gms.maps.model.Polyline as MapLine
import com.google.android.gms.maps.model.PolylineOptions as MapLineBuilder

class MapsActivity : PUCMapsActivity(), OnMapReadyCallback {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val onCircleClickListener = GoogleMap.OnCircleClickListener {
        requireNotNull(it) {
            "Clicked on circle, but circle is null"
        }

        pathEditor?.onCircleClicked(it)
    }
    private val onPolylineClickListener = GoogleMap.OnPolylineClickListener {
        requireNotNull(it) {
            "Clicked on polyline, but polyline is null"
        }

        pathEditor?.onLineClicked(it)
    }
    private val onMapClickListener = GoogleMap.OnMapClickListener {
        requireNotNull(it) {
            "Clicked on map, but got null latlng"
        }

        pathEditor?.onMapClicked(it)
    }

    private var mapContainer: MapContainer? = null
    private var pathEditor: PathEditorHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        supportFragmentManager
            .findFragmentById(R.id.map)
            .let { it as SupportMapFragment }
            .getMapAsync(this)

        settings_button.setOnClickListener {
            val settings = pathEditor?.settings ?: return@setOnClickListener
            showPathEditorSettings(settings)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_maps, menu)
        return true
    }

    override fun onDestroy() {
        mapContainer?.removeOnCircleClickListener(onCircleClickListener)
        mapContainer?.removeOnPolylineClickListener(onPolylineClickListener)
        mapContainer?.removeOnMapClickListener(onMapClickListener)

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        if (pathEditor != null && mapContainer != null) {
            applyWindowInsets(mapContainer!!)
        }
    }


    private fun onSaveMap() {
        val progressDialog = ProgressDialog.show(
            fragmentManager = supportFragmentManager,
            isIndeterminated = true,
            titleRes = R.string.action_saving,
            messageRes = R.string.action_wait
        )

        saveEverything { success ->
            progressDialog.dismissAllowingStateLoss()

            if (!success) {
                // TODO: Show error message
            }
        }
    }

    private fun onSettingsChanged(editorSettings: PathEditorSettings) {
        pathEditor?.settings = editorSettings

        delete_button.hide()
        edit_button.hide()

        when (editorSettings.shape) {
            PathEditorShape.Circle -> {
                edit_button.setOnClickListener {
                    val vertex = pathEditor?.selectedVertex ?: return@setOnClickListener
                    showVertexDetails(vertex)
                }
            }
            PathEditorShape.Line -> {
                delete_button.setOnClickListener { pathEditor?.removeSelectedLine() }
            }
            null -> {
                delete_button.setOnClickListener(null)
                edit_button.setOnClickListener(null)
            }
        }
    }

    private fun onCircleSelectionChanged(circle: MapCircle?) {
        if (circle != null) {
            edit_button.show()
        } else {
            edit_button.hide()
        }
    }

    private fun onPolylineSelectedDidChange(polyline: MapLine?) {
        if (polyline != null) {
            edit_button.show()
        } else {
            edit_button.hide()
        }
    }


    private fun applyWindowInsets(mapContainer: MapContainer) {
        bottom_guideline.applyWindowInsets { insets ->
            val bottomInset = insets.systemWindowInsets.bottom
            val topInset = insets.systemWindowInsets.top

            bottom_guideline.setGuidelineEnd(bottomInset)
            top_guideline.setGuidelineEnd(bottomInset)
            mapContainer.map.setPadding(0, topInset, 0, bottomInset)
        }
    }

    private fun showPathEditorSettings(currentSettings: PathEditorSettings) {
        PathEditorSettingsBottomSheet(currentSettings).apply {
            onSettingsChanged = this@MapsActivity::onSettingsChanged
            onSaveMap = this@MapsActivity::onSaveMap

            show(supportFragmentManager, "edit_path_settings_bottom_sheet")
        }
    }

    private fun showVertexDetails(vertex: GPSPoint) {
        VertexDetailsBottomSheet(vertex)
            .show(supportFragmentManager, "vertex_details_bottom_sheet")
    }


    // region CRUD functions

    private suspend fun saveEdge(edge: GPSLine) {
        FirebaseFirestoreAPI.saveEdge(edge)
    }

    private suspend fun saveVertex(vertex: GPSPoint) {
        FirebaseFirestoreAPI.saveVertex(vertex)
    }

    private suspend fun savePlace(place: Place) {
        FirebaseFirestoreAPI.savePlace(place)
    }


    private suspend fun getEdges(): List<GPSLine> {
        return FirebaseFirestoreAPI.getEdgeList()
    }

    private suspend fun getPlaces(): List<Place> {
        return FirebaseFirestoreAPI.getPlaceList()
    }

    private suspend fun getVertexes(): List<GPSPoint> {
        return FirebaseFirestoreAPI.getVertexList()
    }

    private fun saveEverything(callback: (Boolean) -> Unit = {}) {
        val pathEditor = pathEditor

        if (pathEditor == null) {
            callback(false)
            return
        }

        val vertexList = pathEditor.vertices
        val edgeList = pathEditor.edges

        ioScope.launch {
            FirebaseFirestoreAPI.deleteVertexList()
            FirebaseFirestoreAPI.deleteEdgeList()

            val vertexSavingJob = launch {
                vertexList.forEach { saveVertex(it) }
            }

            val edgeSavingJob = launch {
                edgeList.forEach { saveEdge(it) }
            }

            vertexSavingJob.join()
            edgeSavingJob.join()

            callback(true)
        }
    }

    // endregion


    // region UI Callbacks

    @SuppressWarnings("ResourceType")
    override fun onMapReady(googleMap: GoogleMap) {
        val pucLatLng = MapCoordinates(-19.920972, -43.991340)
        val zoom = 15.5f

        mapContainer = MapContainer(googleMap).apply {
            isMapClickEnabled = true
            isCircleClickEnabled = true
            isPolylineClickEnabled = true

            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            map.isMyLocationEnabled = false

            applyWindowInsets(this)

            MapCameraPosition
                .fromLatLngZoom(pucLatLng, zoom)
                .let { CameraUpdateFactory.newCameraPosition(it) }
                .let { map.moveCamera(it) }

            addOnCircleClickListener(onCircleClickListener)
            addOnPolylineClickListener(onPolylineClickListener)
            addOnMapClickListener(onMapClickListener)
        }

        // Load data from Firebase and initializes PathEditorHelper instance
        settings_button.hide()

        ioScope.launch {
            launch { drawPlaces(getPlaces()) }

            val vertexListAsync = async { getVertexes() }
            val edgeListAsync = async { getEdges() }

            val vertexList = vertexListAsync.await()
            val edgeList = edgeListAsync.await()

            launch(Dispatchers.Main) {
                pathEditor = PathEditorHelper(
                    PathEditorSettings(),
                    vertexList,
                    edgeList,
                    this@MapsActivity::drawCircle,
                    this@MapsActivity::drawLine
                ).apply {
                    onCircleSelectedDidChange = this@MapsActivity::onCircleSelectionChanged
                    onPolylineSelectedDidChange = this@MapsActivity::onPolylineSelectedDidChange
                }

                settings_button.show()
            }
        }
    }

    private fun drawCircle(center: MapCoordinates): MapCircle? {
        return mapContainer?.map?.drawCircle(center)
    }

    private fun drawLine(from: MapCoordinates, to: MapCoordinates): MapLine? {
        return mapContainer?.map?.drawLine(from, to)
    }

    private fun drawPlace(place: Place): MapMarker? {
        return mapContainer?.map?.drawPlace(place)
    }

    private fun drawPlaces(places: List<Place>): List<MapMarker> {
        return places.mapNotNull(::drawPlace)
    }

    // endregion


    // region Extensions

    private fun GoogleMap.drawPlace(place: Place): MapMarker {
        return MapMarkerBuilder()
            .position(place.gpsPoint.asLatLng())
            .draggable(false)
            .title(place.name)
            .let { this.addMarker(it) }
    }

    private fun GoogleMap.drawCircle(centerAt: MapCoordinates): MapCircle {
        return MapCircleBuilder()
            .center(centerAt)
            .radius(MAP_CIRCLE_RADIUS)
            .let { this.addCircle(it) }
    }

    private fun GoogleMap.drawLine(from: MapCoordinates, to: MapCoordinates): MapLine {
        return MapLineBuilder()
            .color(MAP_LINE_COLOR)
            .width(MAP_LINE_WIDTH)
            .add(from, to)
            .let { addPolyline(it) }
    }

    private fun GPSPoint.asLatLng() = MapCoordinates(
        latitude.degrees,
        longitude.degrees
    )

    private inline fun <T : View> T.applyWindowInsets(
        crossinline action: T.(WindowInsetsCompat) -> Unit
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(bottom_guideline) { _, insets ->
            action(insets)
            insets
        }
    }

    // endregion


    companion object {
        private const val MAP_CIRCLE_RADIUS = 2.0

        private const val MAP_LINE_COLOR = Color.BLUE
        private const val MAP_LINE_WIDTH = 15f
    }
}