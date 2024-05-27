package com.diplom.tabletkaapp.view_models.list.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.holders.MedicineHolder
import models.Medicine

class MedicineAdapter(val medicineList: MutableList<AbstractModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMedicineBinding.inflate(inflater, parent, false)
        return MedicineHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MedicineHolder).bind(medicineList[position] as Medicine)
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }
}