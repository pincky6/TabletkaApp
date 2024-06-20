package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Hospital

/**
 * Класс для работы с базой данных аптек(списка желания аптек) в Firebase Realtime Database
 */
object FirebaseHospitalDatabase: TabletkaDatabase {
    /**
     * Объект для получения ссылки на нужный документ в Firebase Realtime Database
     */
    val pharmacyDatabase: DatabaseReference
        get() {
            val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
            return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("pharmacy")
        }

    /**
     * Метод для чтение всего списка желания аптек
     * @param list список аптек
     * @param onCompleteListener слушатель завершения работы
     * @param onReadCancelled слушатель завершения чтения данных
     */
    override fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        pharmacyDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(pharmacy in snapshot.children){
                    pharmacy.getValue<Hospital>()?.let {
                        list.add(it as Hospital)
                        list.last().id = pharmacy.key!!
                    }
                }
                onCompleteListener.complete(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }

    /**
     * Метод для добавления аптеки в список желаний
     * @param model аптека
     * @param requestId идентификатор запроса
     * @param regionId идентификатор региона
     * @param query запрос
     */
    override fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String) {
        val pharmacy = model as Hospital
        pharmacyDatabase.child(model.id + "-" + requestId +
                "-" + regionId + "-" + query + "-" + "hospital"
        ).setValue(pharmacy)
    }

    /**
     * Метод для удаления аптеки
     * @param model аптека
     * @param requestId идентификатор запроса
     * @param regionId идентификатор региона
     * @param query запрос
     */
    override fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String){
        pharmacyDatabase.child(model.id + "-" + requestId +
                "-" + regionId + "-" + query + "-" + "hospital").removeValue()
    }

    /**
     * Метод для генерации уникального ключа
     */
    override fun generateKey(): String {
        return pharmacyDatabase.push().key ?: "null_key"
    }
}