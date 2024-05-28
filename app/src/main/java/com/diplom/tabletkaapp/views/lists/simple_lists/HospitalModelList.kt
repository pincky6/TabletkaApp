package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.util.CacheMedicineConverter
import com.diplom.tabletkaapp.view_models.HospitalModelListViewModel
import com.diplom.tabletkaapp.view_models.cache.MedicineCacher
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
//        CoroutineScope(Dispatchers.IO).launch {
//            val medicineEntities = model.database.medicineDao().getMedicineByRecordId(requestId)
//            initRecyclerViewWithMainContext(CacheMedicineConverter.fromEntityListToModelList(medicineEntities))
//
//            val medicineList = MedicineParser.parseFromName(query, regionId)
//            MedicineCacher.validateMedicineDatabase(model.database, requestId,
//                medicineList, medicineEntities)
//            initRecyclerViewWithMainContext(medicineList)
//        }
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
        binding.medicineInfo.visibility = View.GONE
    }
}