package com.diplom.tabletkaapp.views.lists.holders

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.diplom.tabletkaapp.databinding.SearchItemBinding
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Класс для описания элементов запросов
 */
class SuggestionHolder(var binding: SearchItemBinding): RecyclerView.ViewHolder(binding.root) {
    /**
     * Метод привзяки данных модели к ее представлению
     * Установка текста
     * @param requestEntity  сущность запроса
     * @param update метод обновления
     */
    fun bind(requestEntity: RequestEntity, update: () -> Unit){
        binding.searchItemText.text = requestEntity.request
        binding.deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Room.databaseBuilder(
                    binding.root.context.applicationContext,
                    AppDatabase::class.java,
                    DatabaseInfo.DATABASE_NAME
                ).build().requestDao().deleteRequest(requestEntity)
                update.invoke()
            }
        }
    }
}