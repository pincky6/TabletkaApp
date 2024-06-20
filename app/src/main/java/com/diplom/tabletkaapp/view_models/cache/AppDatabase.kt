package com.diplom.tabletkaapp.view_models.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.models.cache_data_models.converters.ExpirationDatesConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PackagesNumberConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PriceRangeConverter
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.cache.medicine_daos.MedicineDao
import com.diplom.tabletkaapp.view_models.cache.pharmacy_daos.HospitalDao

/**
 * Класс описывающий базу данных приложения
 */
@Database(entities = [
    MedicineEntity::class,
    HospitalEntity::class,
    RequestEntity::class
], version = DatabaseInfo.DATABASE_VERSION, exportSchema = false)
@TypeConverters(PriceRangeConverter::class,
                ExpirationDatesConverter::class,
                PackagesNumberConverter::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Метод для получения таблицы медикаментов
     */
    abstract fun medicineDao(): MedicineDao

    /**
     * Метод для получения таблицы аптек
     */
    abstract fun hospitalDao(): HospitalDao

    /**
     * Метод для получения таблицы запросов
     */
    abstract fun requestDao(): RequestDao
}