package com.diplom.tabletkaapp.view_models.list.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Query
import com.diplom.tabletkaapp.models.AbstractModel

class HospitalAdapter(override var list: MutableList<AbstractModel>?, query: String, val regionId: Int,
                      medicineId: Long, requestId: Long): AbstractAdapter(list) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}