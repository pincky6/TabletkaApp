package com.diplom.tabletkaapp.view_models.cache.pharmacy_daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import models.Hospital

@Dao
interface HospitalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospital(hospital: HospitalEntity)

    @Query("DELETE FROM hospitals")
    suspend fun deleteAll()

    @Query("SELECT * FROM hospitals WHERE id = :id")
    suspend fun getMedicineById(id: String): HospitalEntity

    @Query("SELECT * FROM hospitals WHERE regionId = :regionId")
    suspend fun getMedicineByRegion(regionId: Int): List<HospitalEntity>

    @Query("SELECT * FROM hospitals")
    suspend fun getAllHospitals(): List<HospitalEntity>

    @Query("SELECT * FROM hospitals WHERE regionId = :regionId AND medicineId = :medicineId AND recordId = :recordId")
    suspend fun getHospitalsByRegionIdAndMedicineIdAndRecordId(regionId: Int, medicineId: Long, recordId: Long): List<HospitalEntity>
    @Query("SELECT MAX(pageId) FROM hospitals")
    suspend fun getMaxPage(): Int
}