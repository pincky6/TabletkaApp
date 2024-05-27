package com.diplom.tabletkaapp.views.lists.simple_lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import models.Medicine

class MedicineModelViewModel: ViewModel() {
    lateinit var database: AppDatabase
    lateinit var medicineList: MutableList<AbstractModel>
    val listFilterViewModel = ListFilterViewModel()
}