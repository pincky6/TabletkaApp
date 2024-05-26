package com.diplom.tabletkaapp.view_models.cache.medicine_daos

import androidx.room.*
import com.diplom.tabletkaapp.models.MedicineEntity
@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)
    @Query("DELETE FROM medicine")
    suspend fun deleteAll()
    @Query("SELECT * FROM medicine WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity
    @Query("SELECT * FROM medicine WHERE record_id = :record_id")
    suspend fun getMedicineByRecordId(record_id: Long)
    @Query("SELECT * FROM medicine")
    suspend fun getAllMedicines(): List<MedicineEntity>
}