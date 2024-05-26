package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicineModelList:
    AbstractModelList() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        binding.updateButton.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val medicineList = MedicineParser.parseFromName("", 0)

        }
        initFilterButton {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}