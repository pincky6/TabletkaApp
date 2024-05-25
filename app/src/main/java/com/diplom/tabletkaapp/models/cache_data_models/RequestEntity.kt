package com.diplom.tabletkaapp.models.cache_data_models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request")
data class RequestEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val request: String,
    val requestType: Int
)