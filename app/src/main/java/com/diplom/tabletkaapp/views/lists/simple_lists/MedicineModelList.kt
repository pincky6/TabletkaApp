package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.util.CacheMedicineConverter
import com.diplom.tabletkaapp.view_models.cache.MedicineCacher
import com.diplom.tabletkaapp.view_models.list.adapters.MedicineAdapter
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Medicine

class MedicineModelList:
    AbstractModelList() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initGetFilter(false)

        hideUselessUI()

        val query = arguments?.getString("query") ?: ""
        val requestId = arguments?.getLong("requestId") ?: 0

        val regionId = arguments?.getInt("regionId") ?: 0
        CoroutineScope(Dispatchers.IO).launch {
            val medicineEntities = model.database.medicineDao().getMedicineByRecordId(requestId)
            val convertedList = CacheMedicineConverter.fromEntityListToModelList(medicineEntities)
            initRecyclerViewWithMainContext(MedicineAdapter(convertedList, query, regionId, requestId, null), convertedList)

            val medicineList = MedicineParser.parseFromName(query, regionId)
            MedicineCacher.validateMedicineDatabase(model.database, requestId,
                                                    medicineList, medicineEntities)
            initRecyclerViewWithMainContext(MedicineAdapter(medicineList, query, regionId, requestId, null), medicineList)
        }
        binding.filterButton.text = context?.getString(R.string.hospital_filter_and_sort_button)
        initFilterButton()
        return binding.root
    }

    private fun initFilterButton(){
        initFilterButton {
            findNavController(binding.root).navigate(
                MedicineModelListDirections.showListFilterDialogFragmentMedicine(false,
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