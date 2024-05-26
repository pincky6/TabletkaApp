package com.diplom.tabletkaapp.view_models.cache

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.MedicineEntity
import models.Medicine

object MedicineCacher {
    fun add(appDatabase: AppDatabase, medicine: Medicine, requestId: Long){
        val medicineDao = appDatabase.medicineDao()
        val medicineEntity = MedicineEntity(medicine.id, medicine.name, medicine.medicineReference,
            medicine.compound, medicine.compoundReference, medicine.recipe, medicine.recipeInfo, medicine.companyName,
            medicine.companyReference, medicine.country, medicine.priceRange, medicine.hospitalCount, requestId)
        medicineDao.insertMedicine(medicineEntity)
    }
    suspend fun deleteById(appDatabase: AppDatabase, requestId: Long){
        val medicineDao = appDatabase.medicineDao()
        medicineDao.deleteByRecordId(requestId)
    }
    suspend fun addMedicineList(appDatabase: AppDatabase, medicineList: MutableList<AbstractModel>, requestId: Long){
        for(medicine in medicineList){
            add(appDatabase, medicine as Medicine, requestId)
        }
    }
    suspend fun isValidData(appDatabase: AppDatabase, medicineList: MutableList<AbstractModel>): Boolean{
        val medicineEntities = appDatabase.medicineDao().getAllMedicines()
        if(medicineList.size != medicineEntities.size) return false
        for(i in medicineEntities.indices){
            val medicine = medicineList[i] as Medicine
            val medicineEntity = medicineEntities[i]
            if(medicine.name              != medicineEntity.name ||
               medicine.medicineReference != medicineEntity.medicineReference ||
               medicine.compound          != medicineEntity.compound ||
               medicine.compoundReference != medicineEntity.compoundReference ||
               medicine.recipe            != medicineEntity.recipe ||
               medicine.recipeInfo        != medicineEntity.recipeInfo ||
               medicine.companyName       != medicineEntity.companyName ||
               medicine.companyReference  != medicineEntity.companyReference ||
               medicine.country           != medicineEntity.country ||
               medicine.priceRange        != medicineEntity.priceRange ||
               medicine.hospitalCount     != medicineEntity.hospitalCount
              ){
                return false
            }
        }
        return true
    }

}