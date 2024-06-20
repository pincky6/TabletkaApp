package com.diplom.tabletkaapp.util

import android.content.Context
import com.diplom.tabletkaapp.R
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import java.time.Duration
import java.util.Calendar
import java.util.Date

/**
 * Утилита для работ с картам
 */
object MapUtil {
    //средняя скорость машины
    const val AVERAGE_CAR_SPEED:  Double = 60.0
    //средняя скорость пешехода
    const val AVERAGE_FOOT_SPEED: Double = 6.0
    //средняя скорость самоката/скутера
    const val AVERAGE_VELO_SPEED: Double = 20.0

    /**
     * @param flag тип перемещения
     * Метод для получения иконки исходя из флага
     */
    fun getRoadImageFromFlag(flag: String): Int{
        return when(flag){
            OSRMRoadManager.MEAN_BY_FOOT -> R.drawable.baseline_man_24
            OSRMRoadManager.MEAN_BY_BIKE -> R.drawable.baseline_bike_scooter_24
            OSRMRoadManager.MEAN_BY_CAR -> R.drawable.baseline_directions_car_24
            else -> {0}
        }
    }

    /**
     * @param context контекст приложения
     * @param flag тип перемещения
     * Возвращает строку о типе перемещения
     */
    fun getRoadStringFromFlag(context: Context, flag: String): String{
        return when(flag){
            OSRMRoadManager.MEAN_BY_FOOT -> context.getString(R.string.by_foot_text)
            OSRMRoadManager.MEAN_BY_BIKE -> context.getString(R.string.by_velo_text)
            OSRMRoadManager.MEAN_BY_CAR -> context.getString(R.string.by_car_text)
            else -> {""}
        }
    }

    /**
     *@param distance дистанция
     *@param roadType тип перемещения
     * Метод для расчета времени дистанции
     */
    fun calculateDistanceTime(distance: Double, roadType: String): Double {
        return when(roadType){
            OSRMRoadManager.MEAN_BY_FOOT -> (((distance) / AVERAGE_FOOT_SPEED.toDouble()) * 3600)
            OSRMRoadManager.MEAN_BY_BIKE -> (((distance) / AVERAGE_VELO_SPEED.toDouble()) * 3600)
            OSRMRoadManager.MEAN_BY_CAR  -> (((distance) / AVERAGE_CAR_SPEED.toDouble()) * 3600)
            else -> {0.0}
        }
    }

    /**
     * @param distance дистанция
     * @param roadType тип перемещения
     * Метод для получения строки о количестве времени на заданный маршрут
     */
    fun getDistanceTimeAsString(distance: Double, roadType: String): String {
        val seconds = calculateDistanceTime(distance, roadType).toLong()
        val duration = Duration.ofSeconds(seconds)
        val days = duration.toDays()
        val hours = duration.toHours() - duration.toDays() * 24
        val minutes = duration.toMinutes() - duration.toDays() * 24 * 60 - hours * 60
        return "$days дней $hours часов $minutes минуты"
    }
}