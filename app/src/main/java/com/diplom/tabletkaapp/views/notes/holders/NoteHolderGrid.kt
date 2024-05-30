package com.diplom.tabletkaapp.views.notes.holders

import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemNoteGridBinding
import com.diplom.tabletkaapp.models.data_models.Note

class NoteHolderGrid(
    var binding: ItemNoteGridBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note){
        binding.title.text = note.name
        binding.text.text  = note.describe
    }
}