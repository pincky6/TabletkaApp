package com.diplom.tabletkaapp.view_models.adapters.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemHospitalBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.ui.search.holders.HospitalHolder
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Hospital
import models.Medicine

class HospitalRegionAdapter(override var list: MutableList<AbstractModel>?, val appDatabase: AppDatabase,
                      var maxPage: Int,
                      val regionId: Int, override val onWishListClicked: ((Boolean)->Unit)?
) : AbstractAdapter(list, onWishListClicked) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHospitalBinding.inflate(inflater, parent, false)
        return HospitalHolder(binding, false, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            (holder as HospitalHolder).bind(it[position] as Hospital, regionId,
                -1, -1, "", onWishListClicked)
            if(position == it.size - 1){
                downloadPage()
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

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