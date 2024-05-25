package com.diplom.tabletkaapp.models.cache_data_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compound")
data class CompoundEntity(
    @PrimaryKey val compoundReference: String,
    val compound: String
)