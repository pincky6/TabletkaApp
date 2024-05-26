package com.diplom.tabletkaapp.view_models.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.models.cache_data_models.converters.ExpirationDatesConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PackagesNumberConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PriceRangeConverter
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import com.diplom.tabletkaapp.view_models.cache.medicine_daos.MedicineDao
import com.diplom.tabletkaapp.view_models.cache.pharmacy_daos.HospitalDao

@Database(entities = [
    MedicineEntity::class,
    HospitalEntity::class,
    RequestEntity::class
], version = 3)
@TypeConverters(PriceRangeConverter::class,
                ExpirationDatesConverter::class,
                PackagesNumberConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun hospitalDao(): HospitalDao
    abstract fun requestDao(): RequestDao
}