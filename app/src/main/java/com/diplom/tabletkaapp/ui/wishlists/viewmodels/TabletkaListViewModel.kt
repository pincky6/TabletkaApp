package com.diplom.tabletkaapp.ui.wishlists.viewmodels

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.firebase.database.TabletkaDatabase
import com.diplom.tabletkaapp.models.AbstractFirebaseModel

class TabletkaListViewModel : ViewModel() {
    var list: MutableList<AbstractFirebaseModel> = arrayListOf()
    var database: TabletkaDatabase? = null
    fun loadFromDatabase(completeListener: OnCompleteListener,
                         readCancelled: OnReadCancelled){
        database?.readAll(list, completeListener, readCancelled)
    }
    fun remove(firebaseModel: AbstractFirebaseModel){
        database?.delete(firebaseModel)
    }
}