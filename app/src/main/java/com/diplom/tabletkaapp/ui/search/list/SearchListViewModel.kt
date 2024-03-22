package com.diplom.tabletkaapp.ui.search.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractFirebaseModel

class SearchListViewModel: ViewModel() {
    private var _list = MutableLiveData<MutableList<AbstractFirebaseModel>>().apply {
    }
    val list: LiveData<MutableList<AbstractFirebaseModel>> = _list
    suspend fun setList(list: MutableList<AbstractFirebaseModel>){
        _list.postValue(list)
    }
}