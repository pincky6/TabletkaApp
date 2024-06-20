package com.diplom.tabletkaapp.view_models.list.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.holders.MedicineHolder
import models.Medicine

/**
 * Класс для отображения списка медикаментов
 * @param list список медикаментов
 * @param query запрос по которому производится поиск медикаментов
 * @param regionId id региона по которому проводится поиск
 * @param requestId id запроса, по которому производится поиск
 * @param onWishListClicked определяет поведение, если данный адаптер используется в списке желания
 */
class MedicineAdapter(override var list: MutableList<AbstractModel>?,
                      val query: String, val regionId: Int, val requestId: Long,
                      override val onWishListClicked: ((Boolean)->Unit)?
) : AbstractAdapter(list, onWishListClicked) {
    /**
     * Класс создающий представление MedicineHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMedicineBinding.inflate(inflater, parent, false)
        return MedicineHolder(binding, false)
    }

    /**
     * Привязка представления MedicineHolder к модели Medcine
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            (holder as MedicineHolder).bind(it[position] as Medicine, query,
                regionId, requestId, onWishListClicked)
        }
    }

    /**
     * Получение количества элементов списка
     */
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}