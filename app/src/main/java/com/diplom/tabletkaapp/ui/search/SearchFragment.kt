package com.diplom.tabletkaapp.ui.search

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
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentSearchBinding
import com.diplom.tabletkaapp.models.PointModel
import com.diplom.tabletkaapp.ui.search.filter.ListSettingsDialogFragment
import com.diplom.tabletkaapp.ui.search.filter.ListSettingsViewModel
import com.diplom.tabletkaapp.ui.search.list.SearchListFragment
import com.diplom.tabletkaapp.ui.search.listeners.OnMedicineClickListener
import com.diplom.tabletkaapp.ui.search.listeners.OnNavigationButtonClicked
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Pharmacy
import org.osmdroid.util.GeoPoint

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    val binding get() = _binding!!
    private var searchView: SearchView? = null
    private val listSettings: ListSettingsViewModel = ListSettingsViewModel()
    private var searchListFragment: SearchListFragment? = null
    private val model: SearchViewModel = SearchViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        if(model.showMedicineList || model.showPharmacyList)
            initBackButton()
        getParentFragmentManager().setFragmentResultListener(
            ListSettingsDialogFragment.KEYS.LIST_SETTINGS_KEY_ADD, getViewLifecycleOwner()
        ) { requestKey: String?, result: Bundle ->
            listSettings.sortMask = result.getInt("sortMask")
            listSettings.minPrice = result.getDouble("minPrice")
            listSettings.maxPrice = result.getDouble("maxPrice")
            val list = searchListFragment?.searchListViewModel?.getList(model.showPharmacyList && model.showMedicineList)
            if(model.showMedicineList && !model.showPharmacyList){
                searchListFragment?.searchListViewModel?.medicineList?.value?.let {
                    listSettings.sortMedicine(
                        it
                    )
                }
            } else {
                searchListFragment?.searchListViewModel?.pharmacyList?.value?.let {
                    listSettings.sortPharmacy(
                        it
                    )
                }
            }
            list?.let {
                searchListFragment?.setAdapterList(list)
                searchListFragment?.updateUI()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchView()
        initSearchList()
        initMapButton();
        initMenus()
    }
    private fun initSearchView(){
        searchView = binding.toolbar.findViewById(R.id.app_bar_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(!model.showMedicineList && !model.showPharmacyList) {
                    searchListFragment?.loadMedicineFromName(listSettings.title){
                        model.showMedicineList = true
                        initBackButton()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                listSettings.title = newText
                val list = searchListFragment?.searchListViewModel?.getList(model.showMedicineList &&
                        model.showPharmacyList)
                list?.let {
                    searchListFragment?.setAdapterList(listSettings.filterByTitle(it))
                    searchListFragment?.updateUI()
                }
               return false
            }
        })
    }
    private fun initBackButton(){
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            if(model.showMedicineList && !model.showPharmacyList) {
                model.showMedicineList = false
                CoroutineScope(Dispatchers.IO).launch {
                    searchListFragment?.setAdapterList(arrayListOf())
                    searchListFragment?.searchListViewModel?.setMedicineList(arrayListOf())
                    searchListFragment?.updateUI()
                    withContext(Dispatchers.Main){
                        binding.toolbar.navigationIcon = null
                        binding.toolbar.setNavigationOnClickListener(null)
                    }
                }
            } else if(model.showMedicineList && model.showPharmacyList){
                model.showPharmacyList = false
                searchListFragment?.searchListViewModel?.setPharmacyList(arrayListOf())
                searchListFragment?.searchListViewModel?.medicineList?.value?.let {
                    searchListFragment?.setAdapterList(it)
                    searchListFragment?.updateUI()
                }
            }
        }
    }
    private fun initSearchList(){
        searchListFragment =
            getChildFragmentManager().findFragmentById(R.id.search_list_fragment) as SearchListFragment?
        searchListFragment?.onCompanyNameClicked = OnMedicineClickListener{name: String ->
            searchListFragment?.setAdapterList(arrayListOf())
            searchListFragment?.updateUI()
            searchListFragment?.loadMedicineFromName(name){
                model.showMedicineList = true
                model.showPharmacyList = false
                searchView?.setQuery("", false)
                initBackButton()
            }
        }
        searchListFragment?.onMedicineNameClicked = OnMedicineClickListener { name: String ->
            searchListFragment?.setAdapterList(arrayListOf())
            searchListFragment?.updateUI()
            searchListFragment?.loadPharmacyFromName(name){
                model.showMedicineList = true
                model.showPharmacyList = true
                binding.floatingActionButton.visibility = View.VISIBLE
                searchView?.setQuery("", false)
                initBackButton()
            }
        }
        searchListFragment?.onRecipeNameClicked = OnMedicineClickListener { name: String ->
            searchListFragment?.setAdapterList(arrayListOf())
            searchListFragment?.updateUI()
            searchListFragment?.loadPharmacyFromName(name){
                model.showMedicineList = true
                model.showPharmacyList = true
                binding.floatingActionButton.visibility = View.VISIBLE
                searchView?.setQuery("", false)
                initBackButton()
            }
        }
        searchListFragment?.onNavigationButtonClicked = OnNavigationButtonClicked{pointModel: PointModel ->
            val arg = GeoPointsListArgs(arrayListOf())
            arg.geoPointsList.add(pointModel)
            findNavController(binding.root).navigate(
                SearchFragmentDirections.showMapFragment(arg)
            )
        }
    }
    private fun initMenus()
    {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.app_bar_filter) {
                    val geoPoint = getUserLocation()
                    findNavController(binding.root).navigate(
                        SearchFragmentDirections.showListSettingsDialog(
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
        private fun initMapButton(){
        binding.floatingActionButton.setOnClickListener {
            if(model.showMedicineList && model.showPharmacyList){
                searchListFragment?.searchListViewModel?.pharmacyList?.value.let {
                    if (it == null) return@let
                    val arg = GeoPointsListArgs(arrayListOf())
                    for(model in it){
                        val pharmacy = model as Pharmacy
                        arg.geoPointsList.add(PointModel(pharmacy.name, GeoPoint(pharmacy.latitude, pharmacy.longitude)))
                    }
                    findNavController(binding.root).navigate(
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
        val locationManager  = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

}