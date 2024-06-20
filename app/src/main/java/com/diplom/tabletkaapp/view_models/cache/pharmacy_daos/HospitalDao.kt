package com.diplom.tabletkaapp.view_models.cache.pharmacy_daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import models.Hospital
/**
 * Класс для работы с таблицей базой данных аптек
 */
@Dao
interface HospitalDao {
    /**
     * Метод для вставки аптеки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospital(hospital: HospitalEntity)

    /**
     * Удаление всех записей
     */
    @Query("DELETE FROM hospitals")
    suspend fun deleteAll()

    /**
     * Получение аптеки по id
     */
    @Query("SELECT * FROM hospitals WHERE id = :id")
    suspend fun getMedicineById(id: String): HospitalEntity

    /**
     * Получить все аптеки
     */
    @Query("SELECT * FROM hospitals")
    suspend fun getAllHospitals(): List<HospitalEntity>

    /**
     * Получить аптеки по идентификатору регионов, медикаментов и записей
     */
    @Query("SELECT * FROM hospitals WHERE regionId = :regionId AND medicineId = :medicineId AND recordId = :recordId")
    suspend fun getHospitalsByRegionIdAndMedicineIdAndRecordId(regionId: Int, medicineId: Long, recordId: Long): List<HospitalEntity>

    /**
     * Получение максималоной страницы, загруженному по данному запросов
     */
    @Query("SELECT MAX(pageId) FROM hospitals WHERE recordId = :requestId AND regionId = :regionId AND medicineId = :medicineId")
    fun getMaxPage(requestId: Long, regionId: Int, medicineId: Long): Int
}