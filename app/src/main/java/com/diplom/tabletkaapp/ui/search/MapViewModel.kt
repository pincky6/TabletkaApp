package com.diplom.tabletkaapp.ui.search

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.PointModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapViewModel: ViewModel() {
    var geoPoints: MutableList<PointModel>? = arrayListOf()
    var currentGeoPoint: GeoPoint = GeoPoint(0.0, 0.0)
    var currentMapGeoPoint: GeoPoint = GeoPoint(0.0, 0.0)
    var currentMarker: Marker? = null
    var showBottomSheet: Boolean = false

}