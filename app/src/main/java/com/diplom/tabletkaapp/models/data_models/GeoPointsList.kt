package com.diplom.tabletkaapp.models.data_models

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class GeoPointsList(val mutableList: MutableList<GeoPoint>): Serializable {
}