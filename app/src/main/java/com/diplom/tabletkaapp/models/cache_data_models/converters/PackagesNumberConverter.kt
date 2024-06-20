package com.diplom.tabletkaapp.models.cache_data_models.converters

import androidx.room.TypeConverter

/**
 * Класс для конвертации целого числа в строку и обратно(преднгзначен для хранения списка упаковок в Room Database)
 */
class PackagesNumberConverter {
    /**
     * @param value строка
     * Данный метод преобразует строку в список чисел
     */
    @TypeConverter
    fun fromStringToIntList(value: String?): MutableList<Int>? {
        return value?.split(",")?.map { it.toInt() }?.toMutableList()
    }
    /**
     * @param list спсиок целых чисел(упаковок)
     * Данный метод преобразует список целых чисел в строку
     */
    @TypeConverter
    fun intListToString(list: MutableList<Int>?): String? {
        return list?.joinToString(",")
    }
}