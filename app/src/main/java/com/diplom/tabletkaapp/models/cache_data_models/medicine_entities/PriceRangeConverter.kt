package com.diplom.tabletkaapp.models.cache_data_models.medicine_entities

import androidx.room.TypeConverter

class PriceRangeConverter {
    @TypeConverter
    fun fromDoubleList(value: MutableList<Double>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toDoubleList(value: String?): MutableList<Double>? {
        return value?.split(",")?.map { it.toDouble() }?.toMutableList()
    }
}