package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel
import models.Hospital
import models.Medicine

open interface TabletkaDatabase {

    fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    )

    fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String)
    fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String)

    fun add(model: AbstractModel) {
        if(model is Hospital) {
            val hospital = model as Hospital
            FirebaseHospitalDatabase.pharmacyDatabase.child(model.id).setValue(hospital)
        } else {
            val medicine = model as Medicine
            FirebaseMedicineDatabase.medicineDatabase.child(model.id).setValue(medicine)
        }
    }
    fun delete(model: AbstractModel){
        FirebaseMedicineDatabase.medicineDatabase.child(model.id).removeValue()
    }
    fun generateKey(): String
}