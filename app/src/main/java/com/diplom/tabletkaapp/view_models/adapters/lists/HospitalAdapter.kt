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

class HospitalAdapter(override var list: MutableList<AbstractModel>?, val appDatabase: AppDatabase,
                      var maxPage: Int, val query: String,
                      val regionId: Int, val medicine: Medicine,
                      val medicineId: Long, val requestId: Long): AbstractAdapter(list) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHospitalBinding.inflate(inflater, parent, false)
        return HospitalHolder(binding, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            (holder as HospitalHolder).bind(it[position] as Hospital, regionId, medicineId, requestId)
            if(position == it.size - 1){
                downloadPage()
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    private fun downloadPage(){
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