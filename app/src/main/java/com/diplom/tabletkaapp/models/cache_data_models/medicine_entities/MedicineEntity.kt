package com.diplom.tabletkaapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.diplom.tabletkaapp.models.cache_data_models.CompanyEntity
import com.diplom.tabletkaapp.models.cache_data_models.CompoundEntity
import com.diplom.tabletkaapp.models.cache_data_models.CountryEntity
import com.diplom.tabletkaapp.models.cache_data_models.PriceRangeEntity
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val medicineReference: String,
    val compoundReference: String,
    @Embedded(prefix = "compound_")
    val compound: CompoundEntity,
    val recipe: String,
    val recipeInfo: String,
    val companyReference: String,
    @Embedded(prefix = "company_")
    val companyName: CompanyEntity,
    val countryId: Long,
    val hospitalCount: Int,
    val request_id: Int? = null
)