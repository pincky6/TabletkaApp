package com.diplom.tabletkaapp.models.cache_data_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
data class CountryEntity (
    @PrimaryKey(autoGenerate = true) val countryId: Long,
    val name: String
)