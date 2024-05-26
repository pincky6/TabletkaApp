package com.diplom.tabletkaapp.models.cache_data_models.converters

import androidx.room.TypeConverter

class PackagesNumberConverter {
    @TypeConverter
    fun fromStringToIntList(value: String?): MutableList<Int>? {
        return value?.split(",")?.map { it.toInt() }?.toMutableList()
    }

    @TypeConverter
    fun intListToString(list: MutableList<Int>?): String? {
        return list?.joinToString(",")
    }
}