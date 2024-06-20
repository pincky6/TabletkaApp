package com.diplom.tabletkaapp.models.data_models

import org.osmdroid.util.GeoPoint
import java.io.Serializable

/**
 * Класс, описывающий список геопозиций
 */
data class GeoPointsList(val mutableList: MutableList<GeoPoint>): Serializable {
}