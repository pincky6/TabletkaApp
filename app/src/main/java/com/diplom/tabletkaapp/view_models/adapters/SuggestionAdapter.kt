package com.diplom.tabletkaapp.view_models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.ItemEmptyBinding
import com.diplom.tabletkaapp.databinding.ItemNoteGridBinding
import com.diplom.tabletkaapp.databinding.ItemNoteLinearBinding
import com.diplom.tabletkaapp.databinding.SearchItemBinding
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.views.lists.holders.SuggestionHolder
import com.diplom.tabletkaapp.views.notes.holders.EmptyHolder
import com.diplom.tabletkaapp.views.notes.holders.NoteHolderGrid
import com.diplom.tabletkaapp.views.notes.holders.NoteHolderLinear

class SuggestionAdapter(var suggestionList: List<String>): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = SearchItemBinding.inflate(inflate, parent, false)
        return SuggestionHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as SuggestionHolder).bind(suggestionList[position])
    }

    override fun getItemCount(): Int {
        return suggestionList.size ?: 1
    }
}