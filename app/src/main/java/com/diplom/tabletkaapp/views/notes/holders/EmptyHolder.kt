package com.diplom.tabletkaapp.views.notes.holders

import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemEmptyBinding

class EmptyHolder(
    val binding: ItemEmptyBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(){
        binding.textView4.text = "Пусто"
    }
}