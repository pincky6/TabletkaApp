package com.diplom.tabletkaapp.view_models.adapters.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemHospitalBinding
import com.diplom.tabletkaapp.databinding.ItemHospitalShortBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.ui.search.holders.HospitalHolder
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.views.lists.holders.HospitalShortHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Hospital
import models.Medicine

/**
 * Класс для отображения аптек, которые ищутся по региону
 * @param list список аптек
 * @param maxPage количество загруженных страниц
 * @param regionId id региона, по которому поиск проводится
 * @param onWishListClicked определяет поведение, если данный адаптер используется в списке желания
 */
class HospitalRegionAdapter(override var list: MutableList<AbstractModel>?,
                      var maxPage: Int,
                      val regionId: Int, override val onWishListClicked: ((Boolean)->Unit)?
) : AbstractAdapter(list, onWishListClicked) {
    /**
     * Создание представления модели данных аптек
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHospitalShortBinding.inflate(inflater, parent, false)
        return HospitalShortHolder(binding)
    }

    /**
     * Привязка представления HospitalShortHolder к модели HospitalShort
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            (holder as HospitalShortHolder).bind(it[position] as HospitalShort)
            if(position == it.size - 1){
                downloadPage()
            }
        }
    }

    /**
     * Метод для получения количества элементов списка
     */
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
    /**
     * Метод для дозагрузки аптек
     * Вначале проверяется, сколько уже страниц аптек уже загружено
     * Если количество аптек в списке не соответствует количеству загруженных аптек,
     * то ничего не происходит(данные еще не загружены)
     * Иначе загружаем данные и переносим их в кеш
     * Обновляем список
     */
    private fun downloadPage(){
        if(maxPage * 20 != list?.size) return
        maxPage++
        list?.let {
            CoroutineScope(Dispatchers.IO).launch {
                it.addAll(HospitalParser.parseFromRegionAndPage(regionId, maxPage))
                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                }
            }
        }
    }
}