package com.diplom.tabletkaapp.view_models.notes

import com.diplom.tabletkaapp.databinding.FragmentReductNoteBinding
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase

class NotesReductViewModel {
    var note: Note? = null
    fun setNote(binding: FragmentReductNoteBinding){
        if(note?.id == "-1"){
            note = Note()
            note!!.id = FirebaseNotesDatabase.generateKey()
        }
        note!!.name = binding.noteTitle.text.toString()
        note!!.describe = binding.noteDescribe.text.toString()
    }
}