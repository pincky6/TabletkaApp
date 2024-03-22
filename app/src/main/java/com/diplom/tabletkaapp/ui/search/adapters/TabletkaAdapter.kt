package com.diplom.tabletkaapp.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.databinding.ItemPharmacyBinding
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.diplom.tabletkaapp.ui.search.holders.MedicineHolder
import com.diplom.tabletkaapp.ui.search.holders.PharmacyHolder
import com.diplom.tabletkaapp.ui.search.listeners.OnMedicineClickListener
import models.Medicine
import models.Pharmacy

class TabletkaAdapter(
    var list: MutableList<AbstractFirebaseModel>,
    val onCompanyNameClicked: OnMedicineClickListener? = null,
    val onMedicineNameClicked: OnMedicineClickListener? = null
): RecyclerView.Adapter<ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        if((list[position] is Medicine)){
            return TabletkaEnum.MEDICINE_HOLDER.ordinal
        } else {
            return TabletkaEnum.PHARMACY_HOLDER.ordinal
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == TabletkaEnum.MEDICINE_HOLDER.ordinal) {
            val binding = ItemMedicineBinding.inflate(inflater, parent, false)
            MedicineHolder(binding)
        } else {
            val binding = ItemPharmacyBinding.inflate(inflater, parent, false)
            PharmacyHolder(binding, false)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is MedicineHolder) {
            holder.bind(list[position] as Medicine, onCompanyNameClicked, onMedicineNameClicked)
        } else if(holder is PharmacyHolder){
            holder.bind(list[position] as Pharmacy)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}