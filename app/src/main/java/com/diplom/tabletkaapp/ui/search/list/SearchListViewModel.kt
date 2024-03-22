package com.diplom.tabletkaapp.ui.search.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractFirebaseModel

class SearchListViewModel: ViewModel() {
    private var _medicineList = MutableLiveData<MutableList<AbstractFirebaseModel>>()
    val medicineList: LiveData<MutableList<AbstractFirebaseModel>> = _medicineList

    private var _pharmacyList = MutableLiveData<MutableList<AbstractFirebaseModel>>()
    val pharmacyList: LiveData<MutableList<AbstractFirebaseModel>> = _pharmacyList
    fun setMedicineList(list: MutableList<AbstractFirebaseModel>){
        _medicineList.postValue(list)
    }
    fun setPharmacyList(list: MutableList<AbstractFirebaseModel>){
        _pharmacyList.postValue(list)
    }
}