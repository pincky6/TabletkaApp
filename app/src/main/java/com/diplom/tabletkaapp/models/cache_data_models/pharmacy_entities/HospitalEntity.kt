package com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diplom.tabletkaapp.models.cache_data_models.converters.ExpirationDatesConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PackagesNumberConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PriceRangeConverter
import java.util.Date

@Entity(tableName = "hospitals")
data class HospitalEntity(
    @PrimaryKey
    val id: String,
    val wish: Boolean,
    val name: String,
    val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    @TypeConverters(ExpirationDatesConverter::class)
    val expirationDates: MutableList<Date>,
    @TypeConverters(PackagesNumberConverter::class)
    val packagesNumber: MutableList<Int>,
    @TypeConverters(PriceRangeConverter::class)
    val prices: MutableList<Double>,
    val region: String
)