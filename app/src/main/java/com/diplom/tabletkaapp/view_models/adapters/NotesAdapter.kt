package com.diplom.tabletkaapp.view_models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.ItemNoteGridBinding
import com.diplom.tabletkaapp.databinding.ItemNoteLinearBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.views.notes.holders.NoteHolderGrid
import com.diplom.tabletkaapp.views.notes.holders.NoteHolderLinear

class NotesAdapter(
    var list: MutableList<AbstractModel>,
    val noteMode: Int
): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        if(noteMode == 0){
            val binding = ItemNoteLinearBinding.inflate(inflate, parent, false)
            return NoteHolderLinear(binding)
        } else {
            val binding = ItemNoteGridBinding.inflate(inflate, parent, false)
            return NoteHolderGrid(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (noteMode == 0){
            (holder as NoteHolderGrid).bind(list[position] as Note)
        } else {
            (holder as NoteHolderGrid).bind(list[position] as Note)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}