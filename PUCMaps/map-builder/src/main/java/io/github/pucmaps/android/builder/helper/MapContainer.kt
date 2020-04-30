package io.github.pucmaps.android.builder.helper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline

class MapContainer(val map: GoogleMap) {

    private val onCircleClickListener = GoogleMap.OnCircleClickListener {
        fireCircleClickListeners(it)
    }
    private val onMapClickListener = GoogleMap.OnMapClickListener {
        fireMapClickListeners(it)
    }
    private val onPolylineClickListener = GoogleMap.OnPolylineClickListener {
        firePolylineClickListeners(it)
    }

    private val onCircleClickListeners = mutableListOf<GoogleMap.OnCircleClickListener>()
    private val onMapClickListeners = mutableListOf<GoogleMap.OnMapClickListener>()
    private val onPolylineClickListeners = mutableListOf<GoogleMap.OnPolylineClickListener>()

    var isCircleClickEnabled = false
        set(value) {
            field = value
            updateOnCircleClickListener()
        }
    var isMapClickEnabled = false
        set(value) {
            field = value
            updateOnMapClickListener()
        }
    var isPolylineClickEnabled = false
        set(value) {
            field = value
            updateOnPolylineClickListener()
        }


    private fun fireCircleClickListeners(circle: Circle?) {
        val listeners = synchronized(onCircleClickListeners) {
            onCircleClickListeners.toList()
        }

        listeners.forEach { it.onCircleClick(circle) }
    }

    private fun fireMapClickListeners(latLng: LatLng?) {
        val listeners = synchronized(onMapClickListeners) {
            onMapClickListeners.toList()
        }

        listeners.forEach { it.onMapClick(latLng) }
    }

    private fun firePolylineClickListeners(polyline: Polyline?) {
        val listeners = synchronized(onPolylineClickListeners) {
            onPolylineClickListeners.toList()
        }

        listeners.forEach { it.onPolylineClick(polyline) }
    }


    private fun updateOnCircleClickListener() {
        map.setOnCircleClickListener(
            if (isCircleClickEnabled) {
                onCircleClickListener
            } else {
                null
            }
        )
    }

    private fun updateOnMapClickListener() {
        map.setOnMapClickListener(
            if (isMapClickEnabled) {
                onMapClickListener
            } else {
                null
            }
        )
    }

    private fun updateOnPolylineClickListener() {
        map.setOnPolylineClickListener(
            if (isPolylineClickEnabled) {
                onPolylineClickListener
            } else {
                null
            }
        )
    }


    fun addOnCircleClickListener(onCircleClickListener: GoogleMap.OnCircleClickListener) {
        synchronized(onCircleClickListeners) {
            if (onCircleClickListeners.contains(onCircleClickListener)) {
                return
            }

            onCircleClickListeners.add(onCircleClickListener)
        }
    }

    fun addOnMapClickListener(onMapClickListener: GoogleMap.OnMapClickListener) {
        synchronized(onMapClickListeners) {
            if (onMapClickListeners.contains(onMapClickListener)) {
                return
            }

            onMapClickListeners.add(onMapClickListener)
        }
    }

    fun addOnPolylineClickListener(onPolylineClickListener: GoogleMap.OnPolylineClickListener) {
        synchronized(onPolylineClickListeners) {
            if (onPolylineClickListeners.contains(onPolylineClickListener)) {
                return
            }

            onPolylineClickListeners.add(onPolylineClickListener)
        }
    }


    fun removeOnCircleClickListener(onCircleClickListener: GoogleMap.OnCircleClickListener) {
        synchronized(onCircleClickListeners) {
            onCircleClickListeners.remove(onCircleClickListener)
        }
    }

    fun removeOnMapClickListener(onMapClickListener: GoogleMap.OnMapClickListener) {
        synchronized(onMapClickListeners) {
            onMapClickListeners.remove(onMapClickListener)
        }
    }

    fun removeOnPolylineClickListener(onPolylineClickListener: GoogleMap.OnPolylineClickListener) {
        synchronized(onPolylineClickListeners) {
            onPolylineClickListeners.remove(onPolylineClickListener)
        }
    }
}