package com.diplom.tabletkaapp.util

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.flow.first
import models.Medicine
import java.util.UUID


object CacheMedicineConverter {
    fun fromEntityToModel(entity: MedicineEntity): Medicine {
        return Medicine(
                        entity.id, entity.wish,
                        entity.name, entity.medicineReference,
                        entity.compound, entity.compoundReference,
                        entity.recipe, entity.recipeInfo,
                        entity.companyName, entity.companyName,
                        entity.country, entity.priceRange, entity.hospitalCount
                        )
    }
    fun fromEntityListToModelList(medicineEntity: List<MedicineEntity>): MutableList<AbstractModel>{
        val medicineList : MutableList<AbstractModel> = mutableListOf()
        for(entity in medicineEntity){
            medicineList.add(fromEntityToModel(entity))
        }
        return medicineList
    }

}