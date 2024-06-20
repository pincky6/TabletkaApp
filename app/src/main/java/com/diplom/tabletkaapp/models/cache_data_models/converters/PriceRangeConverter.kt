package com.diplom.tabletkaapp.models.cache_data_models.converters

import androidx.room.TypeConverter

/**
 * Класс для конвертации рационального числа в строку и обратно(преднгзначен для хранения списка цен в Room Database)
 */
class PriceRangeConverter {
    /**
     * @param value список цен
     * Данный метод преобразует список рациональных чисел(цен) в строку
     */
    @TypeConverter
    fun fromDoubleList(value: MutableList<Double>?): String? {
        return value?.joinToString(",")
    }
    /**
     * @param value строка
     * Данный метод преобразует строку в список рациональных чисел(цен)
     */
    @TypeConverter
    fun toDoubleList(value: String?): MutableList<Double>? {
        return value?.split(",")?.map { it.toDouble() }?.toMutableList()
    }
}