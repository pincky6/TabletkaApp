package com.diplom.tabletkaapp.view_models.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.models.cache_data_models.medicine_entities.PriceRangeConverter
import com.diplom.tabletkaapp.view_models.cache.medicine_daos.MedicineDao

@Database(entities = [
    MedicineEntity::class,
    RequestEntity::class
], version = 2)
@TypeConverters(PriceRangeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun requestDao(): RequestDao
}