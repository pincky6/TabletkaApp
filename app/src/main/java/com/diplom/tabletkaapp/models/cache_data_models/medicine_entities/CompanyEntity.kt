package com.diplom.tabletkaapp.models.cache_data_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company")
data class CompanyEntity(
    @PrimaryKey val companyReference: String,
    val companyName: String
)