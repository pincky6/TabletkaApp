package com.diplom.tabletkaapp.ui.search

import android.view.View
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.ui.search.listeners.OnMedicineClickListener

class SearchViewModel: ViewModel() {
    var showMedicineList: Boolean = false
    var showPharmacyList: Boolean = false
    val choosedList get() = showMedicineList && showPharmacyList

    var floatButtonVisibleStatus: Int = View.GONE

    var onPharmacySwitchListener: OnMedicineClickListener? = null
    var onRecipeNameClicked: OnMedicineClickListener? = null
}