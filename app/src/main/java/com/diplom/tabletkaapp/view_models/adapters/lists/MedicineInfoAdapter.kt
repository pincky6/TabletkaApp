package com.diplom.tabletkaapp.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemMedicineInfoBinding
import com.diplom.tabletkaapp.ui.search.holders.MedicineInfoHolder
import models.Hospital

/**
 * Класс который описывает информации о лекарствах которые находятся в аптеке
 */
class MedicineInfoAdapter(private var pharmacy: Hospital? = null): RecyclerView.Adapter<MedicineInfoHolder>() {
    /**
     * Создание элемента списка представления имеющихся медикаментов в аптеках
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineInfoHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemMedicineInfoBinding = ItemMedicineInfoBinding.inflate(inflater, parent, false)
        return MedicineInfoHolder(binding)
    }
    /**
     * Привязка представления MedicineInfoHolder к данным об медикаментах в аптеках
     */
    override fun onBindViewHolder(holder: MedicineInfoHolder, position: Int) {
        pharmacy?.let {
            holder.bind(
                pharmacy!!.expirationDates[position],
                pharmacy!!.packagesNumber[position],
                pharmacy!!.prices[position]
            )
        }
    }

    /**
     * Получения количества записей в списке
     */
    override fun getItemCount(): Int {
        if (pharmacy == null) return 0
        return pharmacy!!.expirationDates.size
    }

    /**
     * Установка необходимой аптеки
     */
    fun setPharmacy(pharmacy: Hospital?){
        this.pharmacy = pharmacy
    }
}