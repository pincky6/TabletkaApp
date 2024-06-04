package com.diplom.tabletkaapp.util

import android.content.Context
import com.diplom.tabletkaapp.R
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import java.time.Duration
import java.util.Calendar
import java.util.Date

object MapUtil {
    const val AVERAGE_CAR_SPEED:  Double = 60.0
    const val AVERAGE_FOOT_SPEED: Double = 6.0
    const val AVERAGE_VELO_SPEED: Double = 20.0

    const val DAY_IN_SECOND: Double = 86400.0

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
            OSRMRoadManager.MEAN_BY_FOOT -> (((distance) / AVERAGE_FOOT_SPEED.toDouble()) * 3600)
            OSRMRoadManager.MEAN_BY_BIKE -> (((distance) / AVERAGE_VELO_SPEED.toDouble()) * 3600)
            OSRMRoadManager.MEAN_BY_CAR  -> (((distance) / AVERAGE_CAR_SPEED.toDouble()) * 3600)
            else -> {0.0}
        }
    }

    fun getDistanceTimeAsString(seconds: Double, roadType: String): String {
        val seconds = calculateDistanceTime(seconds, roadType).toLong()
        val duration = Duration.ofSeconds(seconds)
        val days = duration.toDays()
        val hours = duration.toHours() - duration.toDays() * 24
        val minutes = duration.toMinutes() - duration.toDays() * 24 * 60 - hours * 60
        return "$days дней $hours часов $minutes минуты"
    }
}