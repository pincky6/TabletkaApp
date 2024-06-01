package com.diplom.tabletkaapp.views.wishlists.lists

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.util.CacheHospitalConverter
import com.diplom.tabletkaapp.view_models.adapters.lists.WishListAdapter
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import com.diplom.tabletkaapp.view_models.list.adapters.HospitalAdapter
import com.diplom.tabletkaapp.view_models.wish_lists.WishListViewModel
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import com.diplom.tabletkaapp.views.wishlists.lists.HospitalWishListFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.Hospital
import models.Medicine
import org.osmdroid.util.GeoPoint

class HospitalWishListFragment: AbstractModelList() {

    val wishModel: WishListViewModel = WishListViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        hideUselessUI()
        initGetFilter(true)
        binding.filterButton.text = context?.getString(R.string.hospital_filter_and_sort_button)
        wishModel.database = FirebaseHospitalDatabase
        initFirebaseRecylcerView()
        initUpdateButton()
        initFilterButton()
        initFloatingButton()
        return binding.root
    }
    private fun initUpdateButton(){
        binding.updateButton.setText(getText(R.string.update_wish_list))
        binding.updateButton.setOnClickListener {
            loadFromFirebase()
        }
    }
    private fun initFilterButton(){
        initFilterButton {
            findNavController(binding.root).navigate(
                HospitalWishListFragmentDirections.showListFilterDialogFragmentHospitalWish(true,
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.sortMask)
            )
        }
    }

    private fun initFloatingButton(){
        binding.floatingActionButton.setOnClickListener {
            val geoPointsList = GeoPointsList(model.modelList.flatMap {
                val hospital = (it as Hospital)
                mutableListOf(GeoPoint(hospital.latitude, hospital.longitude))
            } as MutableList<GeoPoint>)
            findNavController(binding.root).navigate(
                HospitalWishListFragmentDirections.actionHospitalWishListFragmentToMapFragment(geoPointsList, HospitalsList(model.modelList))
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun hideUselessUI(){
        binding.medicineInfo.visibility = View.GONE
    }
    private fun initFirebaseRecylcerView(){
        wishModel.loadFromDatabase(object : OnCompleteListener {
            override fun complete(list: MutableList<AbstractModel>) {
                if(binding_ == null) return
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = WishListAdapter(list){wish->
                    loadFromFirebase()
                }
                updateFirebaseUI(list)
            }
        },
            object : OnReadCancelled {
                override fun cancel() {

                }

            })
    }
    private fun loadFromFirebase(){
        wishModel.list.clear()
        wishModel.loadFromDatabase(object : OnCompleteListener {
            override fun complete(list: MutableList<AbstractModel>) {
                updateFirebaseUI(list)
            }
        },
            object : OnReadCancelled {
                override fun cancel() {

                }

            })
    }
    fun updateFirebaseUI(list: MutableList<AbstractModel>){
        model.modelList = list
        if(binding != null && binding.recyclerView.adapter != null){
            (binding.recyclerView.adapter as WishListAdapter).resetList(list)
            (binding.recyclerView.adapter as WishListAdapter).notifyDataSetChanged()
        }
    }


}