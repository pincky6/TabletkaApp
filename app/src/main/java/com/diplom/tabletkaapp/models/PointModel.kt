package com.diplom.tabletkaapp.models

import org.osmdroid.util.GeoPoint

data class PointModel(
    val id: Int,
    val name: String,
    val geoPoint: GeoPoint,
    val address: String = "",
    val length: String = "",
    val duration: String = ""
    )
