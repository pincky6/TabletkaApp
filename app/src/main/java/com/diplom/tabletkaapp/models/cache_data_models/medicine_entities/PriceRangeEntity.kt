package com.diplom.tabletkaapp.models.cache_data_models

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "price_range",
    primaryKeys = ["medicineId", "price"],
    indices = [Index(value = ["medicineId"])])
data class PriceRangeEntity(
    val medicineId: Long,
    val price: Double
)