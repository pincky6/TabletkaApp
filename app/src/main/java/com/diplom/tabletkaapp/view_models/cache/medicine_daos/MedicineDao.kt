package com.diplom.tabletkaapp.view_models.cache.medicine_daos

import androidx.room.*
import com.diplom.tabletkaapp.models.MedicineEntity
import models.Medicine

/**
 * Класс для работы с таблицей базой данных медикаментов
 */
@Dao
interface MedicineDao {
    /**
     * Метод для вставки медикамениа в таблицу
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicine(medicine: MedicineEntity): Long
    /**
     * Метод для удаления всех медикаментов
     */
    @Query("DELETE FROM medicine")
    suspend fun deleteAll()

    /**
     * Метод для удаления медикамента с заданным индексом
     */
    @Query("DELETE FROM medicine WHERE record_id = :record_id")
    suspend fun deleteByRecordId(record_id: Long)

    /**
     * Метод для выбора медикамента с заданным id
     */
    @Query("SELECT * FROM medicine WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity

    /**
     * Метод для выборки медикаментов с заданным id записи
     */
    @Query("SELECT * FROM medicine WHERE record_id = :record_id")
    suspend fun getMedicineByRecordId(record_id: Long): List<MedicineEntity>

    /**
     * Метод для выбора всех медикаментов
     */
    @Query("SELECT * FROM medicine")
    suspend fun getAllMedicines(): List<MedicineEntity>
}