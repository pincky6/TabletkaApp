package com.diplom.tabletkaapp.view_models.cache

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import models.Hospital

object HospitalCacher{
    suspend fun add(appDatabase: AppDatabase, hospital: Hospital, regionId: Int, medicineId: Long, requestId: Long){
        val hospitalDao = appDatabase.hospitalDao()
        val hospitalEntity = HospitalEntity(hospital.id, hospital.wish, hospital.name, hospital.hospitalReference,
            hospital.latitude, hospital.longitude, hospital.address, hospital.phone,
            hospital.expirationDates, hospital.packagesNumber, hospital.prices, regionId, medicineId, requestId)
        hospitalDao.insertHospital(hospitalEntity)
    }
    suspend fun deleteById(appDatabase: AppDatabase, requestId: Long){
        val medicineDao = appDatabase.medicineDao()
        medicineDao.deleteByRecordId(requestId)
    }
    suspend fun addMedicineList(appDatabase: AppDatabase, hospitalList: MutableList<AbstractModel>,
                                regionId: Int, medicineId: Long, requestId: Long){
        for(medicine in hospitalList){
            add(appDatabase, medicine as Hospital, regionId, medicineId, requestId)
        }
    }

    suspend fun isValidData(appDatabase: AppDatabase, hospitalList: MutableList<AbstractModel>): Boolean{
        val hospitalEntities = appDatabase.hospitalDao().getAllHospitals()
        if(hospitalList.size != hospitalEntities.size) return false
        for(i in hospitalEntities.indices){
            val hospital = hospitalList[i] as Hospital
            val hospitalEntity = hospitalEntities[i]
            if(hospital.name              !=  hospitalEntity.name ||
               hospital.hospitalReference != hospitalEntity.hospitalReference ||
               hospital.address           != hospitalEntity.address ||
               hospital.phone             != hospitalEntity.phone ||
               hospital.expirationDates            != hospitalEntity.expirationDates ||
               hospital.packagesNumber        != hospitalEntity.packagesNumber ||
               hospital.prices       != hospitalEntity.prices ||
               Math.abs(hospital.latitude - hospitalEntity.latitude) > 0.001 ||
               Math.abs(hospital.longitude - hospitalEntity.longitude) > 0.001
            ){
                return false
            }
        }
        return true
    }

    suspend fun validateMedicineDatabase(
        appDatabase: AppDatabase, regionId: Int, medicineId: Long, requestId: Long,
        hospitalList: MutableList<AbstractModel>,
        hospitalEntities: List<HospitalEntity>
    ){
        if(!isValidData(appDatabase, hospitalList)){
            deleteById(appDatabase, requestId)
            addMedicineList(appDatabase, hospitalList, regionId, medicineId, requestId)

        } else if(hospitalEntities.isEmpty()){
            addMedicineList(appDatabase, hospitalList, regionId, medicineId, requestId)
        }
    }
}