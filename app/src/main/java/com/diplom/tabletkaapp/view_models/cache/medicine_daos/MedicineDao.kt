package com.diplom.tabletkaapp.view_models.cache.medicine_daos

import androidx.room.*
import com.diplom.tabletkaapp.models.MedicineEntity
import models.Medicine

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicine(medicine: MedicineEntity): Long
    @Query("DELETE FROM medicine")
    suspend fun deleteAll()
    @Query("DELETE FROM medicine WHERE record_id = :record_id")
    suspend fun deleteByRecordId(record_id: Long)
    @Query("SELECT * FROM medicine WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity
    @Query("SELECT * FROM medicine WHERE record_id = :record_id")
    suspend fun getMedicineByRecordId(record_id: Long): List<MedicineEntity>
    @Query("SELECT * FROM medicine")
    suspend fun getAllMedicines(): List<MedicineEntity>
}