package com.diplom.tabletkaapp.util

import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.PriceRangeEntity
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.flow.first
import models.Medicine
import java.util.UUID


object CacheMedicineConverter {
    suspend fun fromEntityToModel(appDatabase: AppDatabase, entity: MedicineEntity): Medicine {
        val medicineDao = appDatabase.medicineDao()
        val priceRangeEntity: MutableList<Double> = medicineDao.getPriceRangesByMedicineId(entity.id).first().map {
            it.price
        } as MutableList<Double>
        val countryEntity = medicineDao.getCountryById(entity.countryId).first()
        return Medicine(
                        UUID.randomUUID().toString(),
                        entity.name, entity.medicineReference,
                        entity.compound.compound, entity.compound.compoundReference,
                        entity.recipe, entity.recipeInfo,
                        entity.companyName.companyName, entity.companyName.companyReference,
                        countryEntity.name, priceRangeEntity, entity.hospitalCount
                        )
    }

}