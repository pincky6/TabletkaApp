package com.diplom.tabletkaapp.view_models.cache

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.MedicineEntity
import models.Medicine
/**
 * Класс для работы с кешем медикаментов
 */
object MedicineCacher {
    /**
     * Метод для добавления медикаментов в таблицу с аптеками
     * @param appDatabase даза данных
     * @param medicine медикаменты
     * @param requestId идентификатор запроса
     */
    fun add(appDatabase: AppDatabase, medicine: Medicine, requestId: Long){
        val medicineDao = appDatabase.medicineDao()
        val medicineEntity = MedicineEntity(medicine.id, medicine.wish, medicine.name, medicine.medicineReference,
            medicine.compound, medicine.compoundReference, medicine.recipe, medicine.recipeInfo, medicine.companyName,
            medicine.companyReference, medicine.country, medicine.priceRange, medicine.hospitalCount, requestId)
        medicineDao.insertMedicine(medicineEntity)
    }
    /**
     * Метод для удаления медикаментов по индентификатору
     * @param appDatabase база данных
     * @param requestId идентификатор запроса
     */
    suspend fun deleteById(appDatabase: AppDatabase, requestId: Long){
        val medicineDao = appDatabase.medicineDao()
        medicineDao.deleteByRecordId(requestId)
    }
    /**
     * Загрузить список медикаментов в кеш
     * @param appDatabase база данных
     * @param medicineList список медикаментов
     * @param requestId идентификатор запроса
     */
    suspend fun addMedicineList(appDatabase: AppDatabase, medicineList: MutableList<AbstractModel>,
                                requestId: Long){
        for(medicine in medicineList){
            add(appDatabase, medicine as Medicine, requestId)
        }
    }
    /**
     * Метод для проверки валидности данных медикаментов
     * @param appDatabase база данных
     * @param medicineList список медикаментов
     */
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

    /**
     * Проверка валидности кеша
     * @param appDatabase база данных
     * @param medicineList список медикаментов
     * @param medicineEntities сущности медикаментов в кеше
     */
    suspend fun validateMedicineDatabase(
        appDatabase: AppDatabase, requestId: Long,
        medicineList: MutableList<AbstractModel>,
        medicineEntities: List<MedicineEntity>
    ){
        if(!isValidData(appDatabase, medicineList)){
            deleteById(appDatabase, requestId)
            addMedicineList(appDatabase, medicineList, requestId)

        } else if(medicineEntities.isEmpty()){
            addMedicineList(appDatabase, medicineList, requestId)
        }
    }

}