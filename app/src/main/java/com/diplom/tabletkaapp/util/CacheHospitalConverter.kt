package com.diplom.tabletkaapp.util

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import models.Hospital
import models.Medicine

object CacheHospitalConverter {
    fun fromEntityToModel(entity: HospitalEntity): Hospital {
        return Hospital(
            entity.id, entity.wish,
            entity.name, entity.hospitalReference,
            entity.latitude, entity.longitude,
            entity.address, entity.phone,
            entity.expirationDates, entity.packagesNumber,
            entity.prices
        )
    }
    fun fromEntityListToModelList(hospitalEntities: List<HospitalEntity>): MutableList<AbstractModel>{
        val medicineList : MutableList<AbstractModel> = mutableListOf()
        for(entity in hospitalEntities){
            medicineList.add(fromEntityToModel(entity))
        }
        return medicineList
    }
}