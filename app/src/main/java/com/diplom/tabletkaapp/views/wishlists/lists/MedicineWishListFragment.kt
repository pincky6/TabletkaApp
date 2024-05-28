package com.diplom.tabletkaapp.views.wishlists.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import com.diplom.tabletkaapp.views.lists.simple_lists.HospitalModelListDirections

class MedicineWishListFragment: AbstractModelList() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        hideUselessUI()
        initUpdateButton()
        initGetFilter(false)
        binding.filterButton.text = context?.getString(R.string.hospital_filter_and_sort_button)

        initFilterButton()
        return binding.root
    }
    private fun initUpdateButton(){

    }
    private fun initFilterButton(){
        initFilterButton {
            Navigation.findNavController(binding.root).navigate(
                HospitalModelListDirections.showListFilterDialogFragmentHospital(true,
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.sortMask)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun hideUselessUI(){
        binding.medicineInfo.visibility = View.GONE
    }
}