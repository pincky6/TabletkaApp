package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Medicine

/**
 * Класс для хранения медикаментов в Firebase Realtime Database
 */
object FirebaseMedicineDatabase: TabletkaDatabase {
    /**
     * Объект для получения ссылки на нужный документ в Firebase Realtime Database
     */
    val medicineDatabase: DatabaseReference
        get() {
        val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
        return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medicine")
    }
    /**
     * Метод для чтение всего списка желания медикаментов
     * @param list список медикаментов
     * @param onCompleteListener слушатель завершения работы
     * @param onReadCancelled слушатель завершения чтения данных
     */
    override fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        medicineDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(medicine in snapshot.children){
                        medicine?.getValue<Medicine>()?.let {
                            list.add(it)
                            list.last().id = medicine.key!!
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
     * Метод для добавления медикаментов в список желания
     * @param model медикамент
     * @param requestId идентификатор запроса
     * @param regionId идентификатор региона
     * @param query запрос
     */
    override fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String) {
        val medicine = model as Medicine
        medicineDatabase.child(medicine.id + "-" + requestId +
                                        "-" + regionId + "-" + query).setValue(medicine)
    }

    /**
     * Метод для удаления медикамента
     * @param model медикамент
     * @param requestId идентификатор запроса
     * @param regionId идентификатор региона
     * @param query запрос
     */
    override fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String){
        medicineDatabase.child(model.id + "-" + requestId +
                              "-" + regionId + "-" + query).removeValue()
    }
    /**
     * Метод для генерации уникального ключа
     */
    override fun generateKey(): String {
        return medicineDatabase.push().key ?: "null_key"
    }
}