package com.diplom.tabletkaapp.view_models.lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
import com.diplom.tabletkaapp.view_models.cache.AppDatabase

/**
 * Модель-представления списка аптек с краткой информацией
 * @param list список моделей
 * @param regionId идентификатор региона
 * @param listFilter модель-представление фильтра
 * @param database база данных
 */
class HospitalRegionListViewModel(
    var list: MutableList<AbstractModel>,
    var regionId: Int,
    val listFilter: ListFilterViewModel,
    var database: AppDatabase? = null
): ViewModel()   {
    /**
     * Конструктор по умолчанию
     */
    constructor(): this(mutableListOf(), 0,
        ListFilterViewModel(), null)
}