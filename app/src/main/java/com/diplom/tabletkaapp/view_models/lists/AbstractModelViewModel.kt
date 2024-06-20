package com.diplom.tabletkaapp.view_models.lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
import com.diplom.tabletkaapp.view_models.cache.AppDatabase

/**
 * Класс модели-представления для списка аптек/медикаментов
 * @param database база данных
 * @param modelList лист моделей
 * @param listFilter модель-представление фильтра
 */
class AbstractModelViewModel: ViewModel() {
    lateinit var database: AppDatabase
    var modelList: MutableList<AbstractModel> = mutableListOf()
    val listFilter = ListFilterViewModel()
}