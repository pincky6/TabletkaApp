package com.diplom.tabletkaapp.views.notes.holders

import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemEmptyBinding

/**
 * Представление уведомляющее о том, что список пустой
 */
class EmptyHolder(
    val binding: ItemEmptyBinding
): RecyclerView.ViewHolder(binding.root) {
    /**
     * Привязка текста к представлению
     */
    fun bind(){
        binding.textView4.text = "Пусто"
    }
}