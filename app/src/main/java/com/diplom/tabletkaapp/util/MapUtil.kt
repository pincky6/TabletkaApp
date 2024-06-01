package com.diplom.tabletkaapp.util

import android.content.Context
import com.diplom.tabletkaapp.R
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import java.util.Calendar
import java.util.Date

object MapUtil {
    const val AVERAGE_CAR_SPEED:  Double = 60.0
    const val AVERAGE_FOOT_SPEED: Double = 6.0
    const val AVERAGE_VELO_SPEED: Double = 20.0

    fun getRoadImageFromFlag(flag: String): Int{
        return when(flag){
            OSRMRoadManager.MEAN_BY_FOOT -> R.drawable.baseline_man_24
            OSRMRoadManager.MEAN_BY_BIKE -> R.drawable.baseline_bike_scooter_24
            OSRMRoadManager.MEAN_BY_CAR -> R.drawable.baseline_directions_car_24
            else -> {0}
        }
    }

    fun getRoadStringFromFlag(context: Context, flag: String): String{
        return when(flag){
            OSRMRoadManager.MEAN_BY_FOOT -> context.getString(R.string.by_foot_text)
            OSRMRoadManager.MEAN_BY_BIKE -> context.getString(R.string.by_velo_text)
            OSRMRoadManager.MEAN_BY_CAR -> context.getString(R.string.by_car_text)
            else -> {""}
        }
    }

    fun calculateDistanceTime(distance: Double, roadType: String): Double {
        return when(roadType){
            OSRMRoadManager.MEAN_BY_FOOT -> (((distance) / AVERAGE_FOOT_SPEED.toDouble())) * 3600
            OSRMRoadManager.MEAN_BY_BIKE -> (((distance) / AVERAGE_VELO_SPEED.toDouble())) * 3600
            OSRMRoadManager.MEAN_BY_CAR  -> (((distance) / AVERAGE_CAR_SPEED.toDouble())) * 3600
            else -> {0.0}
        }
    }
}