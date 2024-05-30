package com.diplom.tabletkaapp.view_models.notes

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase

object NotesViewModel: ViewModel() {
    var list: MutableList<AbstractModel> = mutableListOf()
    fun loadFromFirebase(onCompleteListener: OnCompleteListener, onReadCancelled: OnReadCancelled){
        FirebaseNotesDatabase.readAll(list, onCompleteListener, onReadCancelled)
    }
    fun add(note: Note){
        FirebaseNotesDatabase.add(note)
        list.add(note)
    }
    fun delete(note: Note){
        FirebaseNotesDatabase.delete(note)
        list.remove(note)
    }
}