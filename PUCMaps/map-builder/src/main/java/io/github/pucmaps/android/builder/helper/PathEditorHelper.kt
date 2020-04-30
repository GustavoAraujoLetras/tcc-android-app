package io.github.pucmaps.android.builder.helper

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import io.github.pucmaps.android.builder.model.GPSLine
import io.github.pucmaps.android.builder.model.GPSPoint
import io.github.pucmaps.android.builder.model.toGPSPosition
import io.github.pucmaps.android.maps.builder.helper.PathEditorSettings
import io.github.pucmaps.android.maps.builder.helper.PathEditorShape

class PathEditorHelper(
    settings: PathEditorSettings,
    vertexList: List<GPSPoint> = emptyList(),
    edgeList: List<GPSLine> = emptyList(),
    private inline val drawCircleLambda: (LatLng) -> Circle? = { null },
    private inline val drawLineLambda: (LatLng, LatLng) -> Polyline? = { _, _ -> null }
) {

    private val drawnPolylines = mutableMapOf<GPSLine, Polyline>()
    private val drawnCircles = mutableMapOf<GPSPoint, Circle>()

    private var selectedCircle: Circle? = null
    private var selectedPolyline: Polyline? = null
    private var isDrawingAPath = false

    var settings: PathEditorSettings = settings
        set(value) {
            field = value
            onSettingsChanged()
        }

    var isEditing = false
        set(value) {
            field = value

            if (!field) {
                setSelectedCircle(null)
                setSelectedPolyline(null)
            }
        }

    var isCircleClickEnabled = false
        set(value) {
            field = value
            for (drawCircle in drawnCircles.values) {
                drawCircle.isClickable = value
            }
        }

    var isPolylineClickEnabled = false
        set(value) {
            field = value
            for (drawPolyline in drawnPolylines.values) {
                drawPolyline.isClickable = value
            }
        }

    var onCircleSelectedDidChange: (Circle?) -> Unit = { }
    var onPolylineSelectedDidChange: (Polyline?) -> Unit = { }

    var polylineHighlightedColor = Color.GREEN
    var polylineSelectedColor = Color.YELLOW
    var polylineIdleColor = Color.BLUE

    var vertexHighlightedStrokeColor = Color.GREEN
    var vertexHighlightedFillColor = Color.GREEN
    var vertexSelectedStrokeColor = Color.BLACK
    var vertexSelectedFillColor = Color.BLACK
    var vertexIdleStrokeColor = Color.BLACK
    var vertexIdleFillColor = Color.TRANSPARENT

    val edges: List<GPSLine>
        get() = drawnPolylines.keys.toList()

    val vertices: List<GPSPoint>
        get() = drawnCircles.keys.toList()

    val selectedVertex: GPSPoint?
        get() = drawnCircles
            .keysOf(selectedCircle)
            .firstOrNull()

    val selectedEdge: GPSLine?
        get() = drawnPolylines
            .keysOf(selectedPolyline)
            .firstOrNull()

    init {
        drawVertices(vertexList)
        drawEdges(edgeList)
    }


    fun removeSelectedCircle() {
        val circle = selectedCircle ?: return
        setSelectedCircle(null)

        circle.remove()
        drawnCircles
            .keysOf(circle)
            .forEach { drawnCircles.remove(it) }
    }

    fun removeSelectedLine() {
        val polyline = selectedPolyline ?: return
        setSelectedPolyline(null)

        polyline.remove()
        drawnPolylines
            .keysOf(polyline)
            .forEach { drawnPolylines.remove(it) }
    }


    fun onCircleClicked(circle: Circle) {
        if (!isEditing) return

        when (selectedCircle) {
            null -> {
                isDrawingAPath = true
                setSelectedCircle(circle)
            }
            circle -> {
                isDrawingAPath = false
                setSelectedCircle(null)
            }
            else -> {
                val line = GPSLine(selectedCircle!!.center, circle.center)
                val existingPolyline = drawnPolylines[line]

                if (existingPolyline == null) {
                    drawEdge(line)
                }

                setSelectedCircle(circle)
            }
        }
    }

    fun onLineClicked(polyline: Polyline) {
        if (!isEditing) return

        setSelectedCircle(null)

        when (selectedPolyline) {
            polyline -> setSelectedPolyline(null)
            else -> setSelectedPolyline(polyline)
        }
    }

    fun onMapClicked(latlng: LatLng) {
        if (!isEditing) return
        if (!settings.newCirclesEnabled) return

        val selectedCircle = this.selectedCircle
        val circle = drawVertex(GPSPoint(latlng))

        if (settings.connectCirclesEnabled
            && isDrawingAPath
            && selectedCircle != null
            && circle != null
        ) {
            drawEdge(GPSLine(selectedCircle.center, circle.center))
            setSelectedCircle(circle)
        }
    }


    private fun onSettingsChanged() {
        when (settings.shape) {
            PathEditorShape.Circle -> {
                isEditing = true
                isCircleClickEnabled = true
                isPolylineClickEnabled = false
            }
            PathEditorShape.Line -> {
                isEditing = true
                isCircleClickEnabled = false
                isPolylineClickEnabled = true
            }
            else -> {
                isEditing = false
                isCircleClickEnabled = false
                isPolylineClickEnabled = false
            }
        }
    }

    private fun setSelectedCircle(circle: Circle?) {
        if (circle != null) {
            circle.strokeColor = vertexSelectedStrokeColor
            circle.fillColor = vertexSelectedFillColor
        }

        this.selectedCircle?.strokeColor = vertexIdleStrokeColor
        this.selectedCircle?.fillColor = vertexIdleFillColor
        this.selectedCircle = circle

        onCircleSelectedDidChange(selectedCircle)
    }

    private fun setSelectedPolyline(polyline: Polyline?) {
        if (polyline != null) {
            polyline.color = polylineSelectedColor
        }

        this.selectedPolyline?.color = polylineIdleColor
        this.selectedPolyline = polyline

        onPolylineSelectedDidChange(selectedPolyline)
    }

    private fun setHighlightedCircle(highlighted: Boolean, circle: Circle?) {
        circle ?: return

        val strokeColor = if (highlighted) vertexHighlightedStrokeColor else vertexIdleStrokeColor
        val fillColor = if (highlighted) vertexHighlightedFillColor else vertexIdleFillColor

        circle.strokeColor = strokeColor
        circle.fillColor = fillColor
    }

    private fun setHighlightedPolyline(highlighted: Boolean, polyline: Polyline?) {
        polyline ?: return

        val color = if (highlighted) polylineHighlightedColor else polylineIdleColor
        val strokeWidth = if (highlighted) 10f else 5f

        polyline.color = color
        polyline.width = strokeWidth
    }


    fun setEdgesHighlighted(highlighted: Boolean, edges: List<GPSLine>) {
        this.drawnPolylines
            .filterKeys { edges.contains(it) }
            .values
            .forEach { setHighlightedPolyline(highlighted, it) }
    }


    // region Drawing functions

    private fun drawEdge(edge: GPSLine): Polyline? {
        return drawLineLambda(
            edge.begin.toLatLng(),
            edge.end.toLatLng()
        )?.also {
            it.isClickable = isPolylineClickEnabled
            drawnPolylines[edge] = it
        }
    }

    private fun drawEdges(edges: List<GPSLine>): List<Polyline> {
        return edges.mapNotNull(::drawEdge)
    }

    private fun drawVertex(vertex: GPSPoint): Circle? {
        return drawCircleLambda(vertex.toLatLng())
            ?.also {
                it.isClickable = isCircleClickEnabled
                drawnCircles[vertex] = it
            }
    }

    private fun drawVertices(vertices: List<GPSPoint>): List<Circle> {
        return vertices.mapNotNull(::drawVertex)
    }

    // endregion

    // region Extensions

    private fun GPSPoint.toLatLng(): LatLng {
        return LatLng(this.latitude.degrees, this.longitude.degrees)
    }

    // endregion
}

fun <K, V> Map<K, V>.keysOf(value: V): List<K> {
    return this.asSequence()
        .filter { it.value == value }
        .map { it.key }
        .toList()
}