package com.diplom.tabletkaapp.view_models.adapters.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemHospitalBinding
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.holders.HospitalHolder
import com.diplom.tabletkaapp.ui.search.holders.MedicineHolder
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import models.Hospital
import models.Medicine

/**
 * Класс который отвечает за отображения списка желаний аптек и лекарств
 * @param list список аптек/лекарств
 * @param onWishListClicked функция которая при нажатии на элемент списка может что-то делать(если не null)
 */
class WishListAdapter(override var list: MutableList<AbstractModel>?,
                      override val onWishListClicked: ((Boolean)->Unit)?
) : AbstractAdapter(list, onWishListClicked) {
    /**
     * Возврат типа данных
     * 0 если медикамент
     * 1 если аптека
     */
    override fun getItemViewType(position: Int): Int {
        if(list?.get(position) is Medicine){
            return 0
        } else{
            return 1
        }
    }

    /**
     * Возвращение представления в зависимости от того что это, аптека или медкамент
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == 0) {
            val binding = ItemMedicineBinding.inflate(inflater, parent, false)
            return MedicineHolder(binding, true)
        } else {
            val binding = ItemHospitalBinding.inflate(inflater, parent, false)
            return HospitalHolder(binding, false, true)
        }
    }

    /**
     * Метод привязывает модель Medicine или Hospital к представлению MedicineHolder или HospitalHolder
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            if(holder is MedicineHolder){
                holder.bind((it[position] as Medicine), "", 0, 0, onWishListClicked)
            } else if(holder is HospitalHolder){
                holder.bind((it[position] as Hospital), 0, 0,0, "", onWishListClicked)
            }
        }
    }

    /**
     * Метод возвращает количество элементов списка
     */
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}