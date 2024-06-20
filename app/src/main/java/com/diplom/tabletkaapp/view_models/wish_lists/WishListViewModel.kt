package com.diplom.tabletkaapp.view_models.wish_lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.firebase.database.TabletkaDatabase
import com.diplom.tabletkaapp.models.AbstractModel

/**
 * Класс модель-представления списка желаемого
 */
class WishListViewModel: ViewModel() {
    /**
     * Список желаемого
     */
    var list: MutableList<AbstractModel> = mutableListOf()

    /**
     * База данных
     */
    var database: TabletkaDatabase? = null

    /**
     * Метод загрузки с базы данных Firebase Realtime Database списка желаемого
     */
    fun loadFromDatabase(completeListener: OnCompleteListener,
                         readCancelled: OnReadCancelled
    ){
        database?.readAll(list, completeListener, readCancelled)
    }
}