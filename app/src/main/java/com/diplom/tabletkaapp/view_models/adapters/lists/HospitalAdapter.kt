package com.diplom.tabletkaapp.view_models.list.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Query
import androidx.room.util.query
import com.diplom.tabletkaapp.databinding.ItemHospitalBinding
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.ui.search.holders.HospitalHolder
import com.diplom.tabletkaapp.ui.search.holders.MedicineHolder
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Hospital
import models.Medicine

/**
 * Адаптер аптек
 * @param list список аптек
 * @param appDatabase кещ базы данных
 * @param maxPage максимальное количество загруженных страниц
 * @param query запрос
 * @param regionId заданный регион
 * @param medicine медикамент по которому проводится поиск аптек
 * @param medicineId уникальный идентификатор данного медикамента
 * @param requestId уникальный идентификатор запроса
 * @param onWishListClicked функция, которая позволяет определять поведение
 *                          приложения при использовании данного класса в списке желания
 */
class HospitalAdapter(override var list: MutableList<AbstractModel>?, val appDatabase: AppDatabase,
                      var maxPage: Int, val query: String,
                      val regionId: Int, val medicine: Medicine,
                      val medicineId: Long, val requestId: Long, override val onWishListClicked: ((Boolean)->Unit)?
) : AbstractAdapter(list, onWishListClicked) {
    /**
     * Метод для создания элементов списка аптек
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHospitalBinding.inflate(inflater, parent, false)
        return HospitalHolder(binding, false, false)
    }

    /**
     * Метод для связки представления HospitalHolder с моделью Hospital
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            (holder as HospitalHolder).bind(it[position] as Hospital, regionId,
                    medicineId, requestId, query, onWishListClicked)
            if(position == it.size - 1){
                downloadPage()
            }
        }
    }

    /**
     * метод возвращающий количество элементов списка
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
                it.addAll(HospitalParser.parsePageFromName(medicine.medicineReference, regionId, maxPage))
                HospitalCacher.deleteById(appDatabase, requestId)
                HospitalCacher.addHospitalList(appDatabase, it, regionId, medicineId, requestId)
                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                }
            }
        }
    }
}