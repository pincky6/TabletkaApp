package com.diplom.tabletkaapp.view_models.cache

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.MedicineEntity
import com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities.HospitalEntity
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.views.lists.holders.HospitalShortHolder
import models.Hospital

/**
 * Класс для работы с кешем аптек
 */
object HospitalCacher{
    /**
     * Метод для добавления аптеки в таблицу с аптеками
     * @param appDatabase даза данных
     * @param hospital аптека
     * @param page страница
     * @param regionId идентификатор региона
     * @param requestId идентификатор запроса
     */
    suspend fun add(appDatabase: AppDatabase, hospital: Hospital, page: Int,
                    regionId: Int, medicineId: Long, requestId: Long){
        val hospitalDao = appDatabase.hospitalDao()
        val hospitalEntity = HospitalEntity(hospital.id, hospital.wish, hospital.name, hospital.hospitalReference,
            hospital.latitude, hospital.longitude, hospital.address, hospital.phone,
            hospital.expirationDates, hospital.packagesNumber, hospital.prices, page, regionId, medicineId, requestId)
        hospitalDao.insertHospital(hospitalEntity)
    }

    /**
     * Метод для удаления аптек по индентификатору
     * @param appDatabase база данных
     * @param requestId идентификатор запроса
     */
    suspend fun deleteById(appDatabase: AppDatabase, requestId: Long){
        val medicineDao = appDatabase.medicineDao()
        medicineDao.deleteByRecordId(requestId)
    }

    /**
     * Загрузить список аптек в кеш
     * @param appDatabase даза данных
     * @param hospitalList аптека
     * @param regionId идентификатор региона
     * @param requestId идентификатор запроса
     */
    suspend fun addHospitalList(appDatabase: AppDatabase, hospitalList: MutableList<AbstractModel>,
                                regionId: Int, medicineId: Long, requestId: Long){
        for(i in 0 until hospitalList.size){
            add(appDatabase, hospitalList[i] as Hospital, i % 20, regionId, medicineId, requestId)
        }
    }

    /**
     * Метод для проверки валидности данных аптек
     * @param appDatabase база данных
     * @param hospitalList список аптек
     */
    suspend fun isValidData(appDatabase: AppDatabase, hospitalList: MutableList<AbstractModel>): Boolean{
        if(hospitalList.size != 0 && (hospitalList[0] is HospitalShort)) return false
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

    /**
     * Метод для проверки списка аптек на валидность данных
     * В случае их не валиндности обновляем список
     * @param appDatabase даза данных
     * @param medicineId идентификатор медикамента
     * @param hospitalList список аптек
     * @param hospitalEntities список закешированных аптек
     * @param regionId идентификатор региона
     * @param requestId идентификатор запроса
     */
    suspend fun validateMedicineDatabase(
        appDatabase: AppDatabase, regionId: Int, medicineId: Long, requestId: Long,
        hospitalList: MutableList<AbstractModel>,
        hospitalEntities: List<HospitalEntity>
    ){
        if(!isValidData(appDatabase, hospitalList)){
            deleteById(appDatabase, requestId)
            addHospitalList(appDatabase, hospitalList, regionId, medicineId, requestId)

        } else if(hospitalEntities.isEmpty()){
            addHospitalList(appDatabase, hospitalList, regionId, medicineId, requestId)
        }
    }
}