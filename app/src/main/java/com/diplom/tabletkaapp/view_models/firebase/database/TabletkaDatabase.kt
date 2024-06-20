package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel
import models.Hospital
import models.Medicine

/**
 * Базовый интерфейс для классов наследющих поведение для взаимодействие Firebase Realtime Database
 */
open interface TabletkaDatabase {
    /**
     * Метод который нужно перегрузить
     */
    fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    )
    /**
     * Метод который нужно перегрузить
     */
    fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String)
    /**
     * Метод который нужно перегрузить
     */
    fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String)
    /**
     * Метод для добавления аптеки или медикаментов
     */
    fun add(model: AbstractModel) {
        if(model is Hospital) {
            val hospital = model as Hospital
            FirebaseHospitalDatabase.pharmacyDatabase.child(model.id).setValue(hospital)
        } else {
            val medicine = model as Medicine
            FirebaseMedicineDatabase.medicineDatabase.child(model.id).setValue(medicine)
        }
    }
    /**
     * Метод для удаления аптеки или медикаментов
     */
    fun delete(model: AbstractModel){
        if(model is Hospital){
            FirebaseHospitalDatabase.pharmacyDatabase.child(model.id).removeValue()
        } else {
            FirebaseMedicineDatabase.medicineDatabase.child(model.id).removeValue()
        }
    }
    /**
     * Метод который нужно перегрузить
     */
    fun generateKey(): String
}