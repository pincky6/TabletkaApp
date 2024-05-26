package com.diplom.tabletkaapp.util

import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.flow.first
import models.Medicine
import java.util.UUID


object CacheMedicineConverter {
    fun fromEntityToModel(appDatabase: AppDatabase, entity: MedicineEntity): Medicine {
        return Medicine(
                        entity.id,
                        entity.name, entity.medicineReference,
                        entity.compound, entity.compoundReference,
                        entity.recipe, entity.recipeInfo,
                        entity.companyName, entity.companyName,
                        entity.country, entity.priceRange, entity.hospitalCount
                        )
    }

}