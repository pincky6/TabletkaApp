package com.diplom.tabletkaapp.view_models.lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
import com.diplom.tabletkaapp.view_models.cache.AppDatabase

class HospitalRegionListViewModel(
    var list: MutableList<AbstractModel>,
    var regionId: Int,
    val listFilter: ListFilterViewModel,
    var database: AppDatabase
): ViewModel()   {
    constructor(): this(mutableListOf(), 0,
        ListFilterViewModel())
}