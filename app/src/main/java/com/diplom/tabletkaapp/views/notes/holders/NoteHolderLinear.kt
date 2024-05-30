package com.diplom.tabletkaapp.views.notes.holders

import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemNoteLinearBinding
import com.diplom.tabletkaapp.models.data_models.Note

class NoteHolderLinear(
    var binding: ItemNoteLinearBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note){
        binding.title.text = note.name
        binding.text.text  = note.describe
    }
}