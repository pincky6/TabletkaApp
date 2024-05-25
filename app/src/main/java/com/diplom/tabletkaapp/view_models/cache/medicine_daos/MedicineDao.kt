package com.diplom.tabletkaapp.view_models.cache.medicine_daos

import androidx.room.*
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.CompanyEntity
import com.diplom.tabletkaapp.models.cache_data_models.CompoundEntity
import com.diplom.tabletkaapp.models.cache_data_models.CountryEntity
import com.diplom.tabletkaapp.models.cache_data_models.PriceRangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    // Получение лекарства по ID с связанными объектами
    @Transaction
    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: Long): Flow<MedicineEntity>

    // Вставка нового лекарства
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    // ... другие методы для работы с MedicineEntity (update, delete, etc.)

    // Получение диапазонов цен для лекарства
    @Query("SELECT * FROM price_range WHERE medicineId = :medicineId")
    fun getPriceRangesByMedicineId(medicineId: Long): Flow<List<PriceRangeEntity>>

    // ... другие методы для работы с PriceRangeEntity

    // Получение страны по ID
    @Query("SELECT * FROM country WHERE countryId = :countryId")
    fun getCountryById(countryId: Long): Flow<CountryEntity>

    // ... другие методы для работы с CountryEntity

    // Получение состава по ссылке
    @Query("SELECT * FROM compound WHERE compoundReference = :compoundReference")
    fun getCompoundByReference(compoundReference: String): Flow<CompoundEntity>

    // ... другие методы для работы с CompoundEntity

    // Получение компании по ссылке
    @Query("SELECT * FROM company WHERE companyReference = :companyReference")
    fun getCompanyByReference(companyReference: String): Flow<CompanyEntity>

    // ... другие методы для работы с CompanyEntity
}