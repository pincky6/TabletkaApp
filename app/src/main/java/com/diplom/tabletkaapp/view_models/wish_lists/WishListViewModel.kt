package com.diplom.tabletkaapp.view_models.wish_lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.firebase.database.TabletkaDatabase
import com.diplom.tabletkaapp.models.AbstractModel

class WishListViewModel: ViewModel() {
    val list: MutableList<AbstractModel> = mutableListOf()
    var database: TabletkaDatabase? = null
    fun loadFromDatabase(completeListener: OnCompleteListener,
                         readCancelled: OnReadCancelled
    ){
        database?.readAll(list, completeListener, readCancelled)
    }
}