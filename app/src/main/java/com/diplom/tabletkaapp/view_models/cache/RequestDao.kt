package com.diplom.tabletkaapp.view_models.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import kotlinx.coroutines.flow.Flow

/**
 * Класс описывающий таблицу запросов
 */
@Dao
interface RequestDao {
    /**
     * Метод для вставки запроса
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(request: RequestEntity): Long

    /**
     * Метод для обновления запроса
     */
    @Update
    suspend fun updateRequest(request: RequestEntity)

    /**
     * Метод для удаления запроса
     */
    @Delete
    suspend fun deleteRequest(request: RequestEntity)

    /**
     * Метод удаления конкретного запроса по тексту
     */
    @Query("DELETE FROM request WHERE request = :request")
    fun deleteByRequest(request: String)

    /**
     * Метод удаления запроса по идентификатору
     */
    @Query("SELECT * FROM request WHERE id = :id")
    fun getRequestById(id: String): Flow<RequestEntity>

    /**
     * Метод для выборки всех аптек
     */
    @Query("SELECT * FROM request")
    fun getRequests(): Flow<List<RequestEntity>>

    /**
     * Метод получения запросов по его типу
     */
    @Query("SELECT * FROM request WHERE requestType = :requestType")
    fun getRequestsByType(requestType: Int): Flow<List<RequestEntity>>

    /**
     * Метод получения запросов по типу и тексту
     */
    @Query("SELECT * FROM request WHERE requestType = :requestType AND request = :request")
    fun getRequestsByTypeAndRequest(request: String, requestType: Int): Flow<List<RequestEntity>>

    /**
     * Метод получения запросов, где запрос должен включать в себя request
     */
    @Query("SELECT * FROM request WHERE request LIKE :request")
    fun getRequestsLikeRequest(request: String): List<RequestEntity>
}