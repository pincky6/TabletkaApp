package com.diplom.tabletkaapp.view_models.cache

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(request: RequestEntity): Long

    @Update
    suspend fun updateRequest(request: RequestEntity)

    @Delete
    suspend fun deleteRequest(request: RequestEntity)

    @Query("SELECT * FROM request WHERE id = :id")
    fun getRequestById(id: String): Flow<RequestEntity>

    @Query("SELECT * FROM request")
    fun getRequests(): Flow<List<RequestEntity>>

    @Query("SELECT * FROM request WHERE requestType = :requestType")
    fun getRequestsByType(requestType: Int): Flow<List<RequestEntity>>

    @Query("SELECT * FROM request WHERE requestType = :requestType AND request = :request")
    fun getRequestsByTypeAndRequest(request: String, requestType: Int): Flow<List<RequestEntity>>

    @Query("SELECT * FROM request WHERE request LIKE :request")
    fun getRequestsLikeRequest(request: String): List<RequestEntity>
}