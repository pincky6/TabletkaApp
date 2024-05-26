package com.diplom.tabletkaapp.view_models.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diplom.tabletkaapp.models.MedicineEntity
import models.Medicine

//@Entity(tableName = "medicine")
//data class MedicineEntity(
//    @PrimaryKey
//    val id: String,
//    val name: String,
//    @ColumnInfo(name = "medicine_reference")
//    val medicineReference: String,
//    val compound: String,
//    @ColumnInfo(name = "compound_reference")
//    val compoundReference: String,
//    val recipe: String,
//    @ColumnInfo(name = "recipe_info")
//    val recipeInfo: String,
//    @ColumnInfo(name = "company_name")
//    val companyName: String,
//    @ColumnInfo(name = "company_reference")
//    val companyReference: String,
//    val country: String,
//    @ColumnInfo(name = "price_range")
//    val priceRange: MutableList<Double>,
//    @ColumnInfo(name = "hospital_count")
//    val hospitalCount: Int,
//    @ColumnInfo(name = "record_id")
//    val record_id: Long
//)

object MedicineCacher {
    suspend fun add(appDatabase: AppDatabase, medicine: Medicine, requestId: Long){
        val medicineDao = appDatabase.medicineDao()

        val medicineEntity = MedicineEntity(medicine.id, medicine.name, medicine.medicineReference,
            medicine.compound, medicine.compoundReference, medicine.recipe, medicine.recipeInfo, medicine.companyName,
            medicine.companyReference, medicine.country, medicine.priceRange, medicine.hospitalCount, requestId)
        medicineDao.insertMedicine(medicineEntity)
    }

}