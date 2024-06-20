package com.diplom.tabletkaapp.view_models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.ItemEmptyBinding
import com.diplom.tabletkaapp.databinding.ItemNoteGridBinding
import com.diplom.tabletkaapp.databinding.ItemNoteLinearBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.views.notes.holders.EmptyHolder
import com.diplom.tabletkaapp.views.notes.holders.NoteHolderGrid
import com.diplom.tabletkaapp.views.notes.holders.NoteHolderLinear

/**
 * Класс для отображения списка заметок в спсике
 * @param list список заметок
 * @param noteMode режим отображения
 * @param onUpdateUI функция для обновления ui
 */
class NotesAdapter(
    override var list: MutableList<AbstractModel>?,
    val noteMode: Int,
    val onUpdateUI: () -> Unit
): AbstractAdapter(list, null) {
    /**
     *
     */
    override fun getItemViewType(position: Int): Int {
        if(list?.size == 0){
            return 0
        }
        return 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        if(viewType == 0){
            val binding = ItemEmptyBinding.inflate(inflate, parent, false)
            return EmptyHolder(binding)
        }
        if(noteMode == 0){
            val binding = ItemNoteLinearBinding.inflate(inflate, parent, false)
            return NoteHolderLinear(binding)
        } else {
            val binding = ItemNoteGridBinding.inflate(inflate, parent, false)
            return NoteHolderGrid(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            if (noteMode == 0){
                (holder as NoteHolderLinear).bind(it[position] as Note, onUpdateUI)
            } else {
                (holder as NoteHolderGrid).bind(it[position] as Note, onUpdateUI)
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 1
    }
}