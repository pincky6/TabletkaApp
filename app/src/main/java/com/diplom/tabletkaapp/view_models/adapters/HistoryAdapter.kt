package com.diplom.tabletkaapp.view_models.adapters

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.SearchItemBinding
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.views.lists.holders.SuggestionHolder

class HistoryAdapter(var list: MutableList<RequestEntity>, val update: () -> Unit):
    RecyclerView.Adapter<SuggestionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val inflate = LayoutInflater.from(parent.context)
        return SuggestionHolder(SearchItemBinding.inflate(inflate, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SuggestionHolder, position: Int) {
        (holder).bind(list[position], update)
    }

    fun resetList(newList: MutableList<RequestEntity>){
        list = newList
        notifyDataSetChanged()
    }
}