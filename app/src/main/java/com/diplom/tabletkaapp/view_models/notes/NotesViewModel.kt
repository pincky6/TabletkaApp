package com.diplom.tabletkaapp.view_models.notes

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase

/**
 * Класс описывающий список заметок
 */
object NotesViewModel: ViewModel() {
    /**
     * Список заметок
     */
    var list: MutableList<AbstractModel> = mutableListOf()

    /**
     * Метод для загрузки заметок из Firebase Realtime Database
     * @param onCompleteListener слушатель завершения загрузки заметок
     * @param onReadCancelled слушатель окончания чтения
     */
    fun loadFromFirebase(onCompleteListener: OnCompleteListener, onReadCancelled: OnReadCancelled){
        list.clear()
        FirebaseNotesDatabase.readAll(list, onCompleteListener, onReadCancelled)
    }

    /**
     * Метод для добавления заметки в Firebase Realtime Database
     * @param note заметка
     */
    fun add(note: Note){
        FirebaseNotesDatabase.add(note)
        list.add(note)
    }
    /**
     * Метод для удаления заметки в Firebase Realtime Database
     * @param note заметка
     */
    fun delete(note: Note){
        FirebaseNotesDatabase.delete(note)
        list.remove(note)
    }
}