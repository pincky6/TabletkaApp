package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.Navigation.findNavController
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.util.CacheMedicineConverter
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.MedicineCacher
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.view_models.list.adapters.MedicineAdapter
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MedicineModelList:
    AbstractModelList() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.updateButton.visibility = View.GONE
        val query = arguments?.getString("query") ?: ""
        val requestId = arguments?.getInt("requestId")?.toLong() ?: 0
        val regionId = arguments?.getInt("regionId") ?: 0
        CoroutineScope(Dispatchers.IO).launch {
            val medicineEntities = model.database.medicineDao().getMedicineByRecordId(requestId)
            withContext(Dispatchers.Main){
                model.medicineList = CacheMedicineConverter.fromEntityListToModelList(medicineEntities)
                initRecyclerView(MedicineAdapter(model.medicineList))
            }
            val medicineList = MedicineParser.parseFromName(query, regionId)
            if(!MedicineCacher.isValidData(model.database, medicineList)){
                MedicineCacher.deleteById(model.database, requestId)
                MedicineCacher.addMedicineList(model.database, medicineList, requestId)
                withContext(Dispatchers.Main){
                    model.medicineList = medicineList
                    initRecyclerView(MedicineAdapter(medicineList))
                }
            } else if(medicineEntities.isEmpty()){
                MedicineCacher.addMedicineList(model.database, medicineList, requestId)
                withContext(Dispatchers.Main){
                    model.medicineList = medicineList
                    initRecyclerView(MedicineAdapter(medicineList))
                }
            }
        }
        binding.filterButton.text = context?.getString(R.string.medicine_filter_and_sort_button)
        initFilterButton()
        return binding.root
    }

    private fun initFilterButton(){
        initFilterButton {
            findNavController(binding.root).navigate(
                MedicineModelListDirections.showListFilterDialogFragment(false,
                    model.listFilterViewModel.minPrice.toFloat(),
                    model.listFilterViewModel.minPrice.toFloat(),
                    model.listFilterViewModel.sortMask)
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}