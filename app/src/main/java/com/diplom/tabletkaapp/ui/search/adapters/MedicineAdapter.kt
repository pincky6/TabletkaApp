package com.diplom.tabletkaapp.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.diplom.tabletkaapp.ui.search.holders.MedicineHolder
import models.Medicine

class MedicineAdapter(
    var list: MutableList<AbstractFirebaseModel>
): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMedicineBinding.inflate(inflater, parent, false)
        return MedicineHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as MedicineHolder).bind(list[position] as Medicine)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}