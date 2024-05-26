package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.MedicineCacher
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicineModelList:
    AbstractModelList() {
        lateinit var model: MedicineModelViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        model = MedicineModelViewModel()
        context?.let {
            model.database = Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java,
                "cache3"
            ).build()

        }

        binding.updateButton.visibility = View.GONE
        val query = arguments?.getString("query") ?: ""
        val requestId = arguments?.getInt("requestId")?.toLong() ?: 0
        val regionId = arguments?.getInt("regionId") ?: 0
        CoroutineScope(Dispatchers.IO).launch {
            val medicineList = MedicineParser.parseFromName(query, regionId)
            if(!MedicineCacher.isValidData(model.database, medicineList)){
                MedicineCacher.deleteById(model.database, requestId)
            }
            MedicineCacher.addMedicineList(model.database, medicineList, requestId)
        }
        initFilterButton {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}