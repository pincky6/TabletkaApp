package com.diplom.tabletkaapp.models.cache_data_models

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Класс, представляющий сущность "Запрос".
 *
 * @param id Уникальный идентификатор запроса.
 * @param request Текст запроса.
 * @param requestType Тип запроса (например, MEDICINE, HOSPITAL).
 */
@Entity(tableName = "request")
data class RequestEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val request: String,
    val requestType: Int
)