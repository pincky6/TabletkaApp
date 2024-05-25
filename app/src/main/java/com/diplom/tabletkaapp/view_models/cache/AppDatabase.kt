package com.diplom.tabletkaapp.view_models.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.CompanyEntity
import com.diplom.tabletkaapp.models.cache_data_models.CompoundEntity
import com.diplom.tabletkaapp.models.cache_data_models.CountryEntity
import com.diplom.tabletkaapp.models.cache_data_models.PriceRangeEntity
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.view_models.cache.medicine_daos.MedicineDao

@Database(entities = [
    MedicineEntity::class,
    PriceRangeEntity::class,
    CompoundEntity::class,
    CompanyEntity::class,
    CountryEntity::class,
    RequestEntity::class // Добавьте RequestEntity
], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun requestDao(): RequestDao // Добавьте RequestDao
}