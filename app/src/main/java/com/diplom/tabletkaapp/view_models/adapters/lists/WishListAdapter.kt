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

class WishListAdapter(override var list: MutableList<AbstractModel>?,
                      override val onWishListClicked: (()->Unit)?
) : AbstractAdapter(list, onWishListClicked) {
    override fun getItemViewType(position: Int): Int {
        if(list?.get(position) is Medicine){
            return 0
        } else{
            return 1
        }
    }
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            if(holder is MedicineHolder){
                holder.bind((it[position] as Medicine), "", 0, 0, onWishListClicked)
            } else if(holder is HospitalHolder){
                holder.bind((it[position] as Hospital), 0, 0,0, "", onWishListClicked)
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}