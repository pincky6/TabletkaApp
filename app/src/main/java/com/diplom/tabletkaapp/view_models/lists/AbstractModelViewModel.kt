package com.diplom.tabletkaapp.views.lists.simple_lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
import com.diplom.tabletkaapp.view_models.cache.AppDatabase

class AbstractModelViewModel: ViewModel() {
    lateinit var database: AppDatabase
    var modelList: MutableList<AbstractModel> = mutableListOf()
    val listFilter = ListFilterViewModel()
}