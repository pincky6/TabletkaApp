package com.diplom.tabletkaapp.views.lists.simple_lists

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.util.CacheHospitalConverter
import com.diplom.tabletkaapp.util.CacheMedicineConverter
import com.diplom.tabletkaapp.view_models.HospitalModelListViewModel
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import com.diplom.tabletkaapp.view_models.cache.MedicineCacher
import com.diplom.tabletkaapp.view_models.list.adapters.HospitalAdapter
import com.diplom.tabletkaapp.view_models.list.adapters.MedicineAdapter
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.Medicine

class HospitalModelList:
AbstractModelList() {
    val hospitalModel = HospitalModelListViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initGetFilter(false)

        hideUselessUI()

        val query = arguments?.getString("query") ?: ""
        val requestId = arguments?.getInt("requestId")?.toLong() ?: 0
        val regionId = arguments?.getInt("regionId") ?: 0
        hospitalModel.medicine = arguments?.getSerializable("medicine") as Medicine
        initMedicineInfo()
        CoroutineScope(Dispatchers.IO).launch {
            val hospitalEntities = model.database.hospitalDao().getHospitalsByRegionIdAndMedicineIdAndRecordId(regionId, hospitalModel.medicine.id.toLong(),
                                                                                                               requestId)
            var maxPage = model.database.hospitalDao().getMaxPage(requestId, regionId, hospitalModel.medicine.id.toLong())
            val convertedList = CacheHospitalConverter.fromEntityListToModelList(hospitalEntities)
            initRecyclerViewWithMainContext(HospitalAdapter(convertedList, model.database, maxPage, query, regionId,
                hospitalModel.medicine,
                hospitalModel.medicine.id.toLong(), requestId), convertedList)
            val hospitalList = mutableListOf<AbstractModel>()
            if(maxPage == 0) maxPage++
            for(i in 0 until maxPage) {
                hospitalList.addAll(
                    HospitalParser.parsePageFromName(
                        hospitalModel.medicine.medicineReference,
                        regionId,
                        i + 1
                    )
                )
            }
            HospitalCacher.validateMedicineDatabase(model.database, regionId, hospitalModel.medicine.id.toLong(), requestId,
                hospitalList, hospitalEntities)
            initRecyclerViewWithMainContext(HospitalAdapter(convertedList, model.database, maxPage, query, regionId,
                hospitalModel.medicine,
                hospitalModel.medicine.id.toLong(), requestId), hospitalList)
        }
        binding.filterButton.text = context?.getString(R.string.medicine_filter_and_sort_button)
        initFilterButton()
        return binding.root
    }

    private fun initFilterButton(){
        initFilterButton {
            Navigation.findNavController(binding.root).navigate(
                MedicineModelListDirections.showListFilterDialogFragment(false,
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.sortMask)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun hideUselessUI(){
        binding.updateButton.visibility = View.GONE
    }

    private fun initMedicineInfo(){
        binding.medicineTitle.text = hospitalModel.medicine.name
        binding.copyButton.setOnClickListener{
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Медикамент", hospitalModel.medicine.toString())
            clipboard.setPrimaryClip(clip)
        }
        binding.infoButton.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(
                MedicineModelListDirections.showInfoFragment(hospitalModel.medicine.toString())
            )
        }
    }
}