package com.diplom.tabletkaapp.view_models.firebase.database

import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.firebase.database.TabletkaDatabase
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Hospital

/**
 * Класс для хранения заметок в Firebase Realtime Database
 */
object FirebaseNotesDatabase: TabletkaDatabase {
    /**
     * Объект для получения ссылки на нужный документ в Firebase Realtime Database
     */
    val notesDatabase: DatabaseReference
        get() {
            val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
            return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes")
        }
    /**
     * Метод для чтение всего списка заметок
     * @param list список медикаментов
     * @param onCompleteListener слушатель завершения работы
     * @param onReadCancelled слушатель завершения чтения данных
     */
    override fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        notesDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(pharmacy in snapshot.children){
                    pharmacy.getValue<Note>()?.let { list.add(it as Note) }
                }
                onCompleteListener.complete(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }

    /**
     * пропусти это
     */
    override fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String) {

    }

    /**
     * Метод для добавления заметок в Firebase Realtime Database
     * @param model заметка
     */
    override fun add(model: AbstractModel) {
        val pharmacy = model as Note
        notesDatabase.child(model.id
        ).setValue(pharmacy)
    }
    /**
     * Метод для добавления заметок в Firebase Realtime Database
     * @param model заметка
     * @param onUpdateListener функция для обновления при добавлении заметки
     */
    fun add(model: AbstractModel, onUpdateListener: (()->Unit)?) {
        val pharmacy = model as Note
        notesDatabase.child(model.id
        ).setValue(pharmacy).addOnSuccessListener{
            onUpdateListener?.invoke()
        }
    }

    /**
     * пропусти это
     */
    override fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String) {
    }
    /**
     * Метод для удаления заметок в Firebase Realtime Database
     * @param model заметка
     */
    override fun delete(model: AbstractModel) {
        notesDatabase.child(model.id ).removeValue()

    }
    /**
     * Метод для удаления заметок в Firebase Realtime Database
     * @param model заметка
     * @param onUpdateListener функция для обновления при добавлении заметки
     */
    fun delete(model: AbstractModel, onUpdateListener: (()->Unit)?){
        notesDatabase.child(model.id ).removeValue().addOnSuccessListener {
            onUpdateListener?.invoke()
        }
    }

    /**
     * Генерация уникального идентификатора
     */
    override fun generateKey(): String {
        return notesDatabase.push().key ?: "null_key"
    }
}