package com.diplom.tabletkaapp.ui.wishlists.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentWishListsBinding
import com.diplom.tabletkaapp.models.PointModel
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.ui.search.GeoPointsListArgs
import com.diplom.tabletkaapp.ui.search.SearchFragment
import com.diplom.tabletkaapp.ui.search.SearchFragmentDirections
import com.diplom.tabletkaapp.ui.search.filter.ListFilterDialogFragment
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
import com.diplom.tabletkaapp.ui.wishlists.viewmodels.WishListViewModel
import com.google.android.material.tabs.TabLayout
import models.Pharmacy
import org.osmdroid.util.GeoPoint

class WishListFragment: Fragment() {
    private val model = WishListViewModel()
    private val listSettings: ListFilterViewModel = ListFilterViewModel()
    private val lists = listOf(
        MedicineWishListFragment(),
        PharmacyWishListFragment()
    )
    private var searchView: SearchView? = null
    private var _binding: FragmentWishListsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(model.choosedList == 0){
            parentFragmentManager
                .beginTransaction().replace(R.id.placeHolder, lists[0]).commit()
        } else {
            parentFragmentManager
                .beginTransaction().replace(R.id.placeHolder, lists[1]).commit()
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                parentFragmentManager
                    .beginTransaction().replace(R.id.placeHolder, lists[tab?.position!!]).commit()
                model.choosedList = tab.position
                if(tab.position == 0){
                    binding.floatingActionButton.visibility = View.GONE
                } else {
                    binding.floatingActionButton.visibility = View.VISIBLE
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
        initSearchView()
        initMenus()
        initMapButton()
        initGetFilter()
    }
    private fun initSearchView() {
        searchView = binding.toolbar.findViewById(R.id.app_bar_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                listSettings.title = newText
                val list = lists[model.choosedList].getList()
                list.let {
                    lists[model.choosedList].setAdapterList(listSettings.filterByTitle(it))
                    lists[model.choosedList].updateUI()
                }
                return false
            }
        })
    }
    private fun initMenus() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.app_bar_filter) {
                val geoPoint = getUserLocation()
                val choosedList = model.choosedList != 0
                Navigation.findNavController(binding.root).navigate(
                    SearchFragmentDirections.showListFilterDialog(
                        choosedList,
                        geoPoint,
                        listSettings.sortMask,
                        listSettings.minPrice,
                        listSettings.maxPrice
                    )
                )
            }
            false
        }
    }

    private fun initMapButton() {
        binding.floatingActionButton.setOnClickListener {
            if (model.choosedList == 1) {
                lists[model.choosedList].getList().let {
                    val arg = GeoPointsListArgs(arrayListOf())
                    for (model in it) {
                        val pharmacy = model as Pharmacy
                        arg.geoPointsList.add(
                            PointModel(
                                pharmacy.name,
                                GeoPoint(pharmacy.latitude, pharmacy.longitude)
                            )
                        )
                    }
                    Navigation.findNavController(binding.root).navigate(
                        SearchFragmentDirections.showMapFragment(arg)
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUserLocation(): GeoPoint {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            return location?.let { GeoPoint(it.latitude, location.longitude) } ?: GeoPoint(0.0, 0.0)
        }
        return GeoPoint(0.0, 0.0)
    }

    private fun initGetFilter() {
        getParentFragmentManager().setFragmentResultListener(
            ListFilterDialogFragment.LIST_SETTINGS_KEY_ADD, getViewLifecycleOwner()
        ) { _: String?, result: Bundle ->
            listSettings.sortMask = result.getInt("sortMask")
            listSettings.minPrice = result.getDouble("minPrice")
            listSettings.maxPrice = result.getDouble("maxPrice")
            val choosedList = model.choosedList != 0
            val list = lists[model.choosedList].getList()
            list.let {
                val lst = listSettings.sort(listSettings.filter(it, choosedList), choosedList)
                lists[model.choosedList].setAdapterList(lst)
                lists[model.choosedList].updateUI()
            }
        }
    }
}