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
import com.diplom.tabletkaapp.databinding.FragmentMedicineContentBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.firebase.database.FirebasePharmacyDatabase
import com.diplom.tabletkaapp.models.PointModel
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.parser.PharmacyParser
import com.diplom.tabletkaapp.parser.TabletkaParser
import com.diplom.tabletkaapp.ui.search.filter.ListFilterDialogFragment
import com.diplom.tabletkaapp.ui.search.filter.ListFilterViewModel
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
    private var _binding: FragmentMedicineContentBinding? = null
    val binding get() = _binding!!
    private var searchView: SearchView? = null
    private val listSettings: ListFilterViewModel = ListFilterViewModel()
    private var searchListFragment: SearchListFragment? = null
    private val model: SearchViewModel = SearchViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineContentBinding.inflate(inflater, container, false)
        binding.floatingActionButton.visibility = model.floatButtonVisibleStatus
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initGetFilter()
        initSearchView()
        initSearchList()
        initMapButton();
        initMenus()
    }

    private fun initSearchView() {
        searchView = binding.toolbar.findViewById(R.id.app_bar_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!model.showMedicineList && !model.showPharmacyList) {
                    loadFromTabletka(MedicineParser, listSettings.title,
                                     true, false,){
                        initBackButton()
                    }
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                listSettings.title = newText
                val list = searchListFragment?.getList(
                    model.showMedicineList &&
                            model.showPharmacyList
                )
                list?.let {
                    searchListFragment?.setAdapterList(listSettings.filterByTitle(it))
                    searchListFragment?.updateUI()
                }
                return false
            }
        })
    }

    private fun initBackButton() {
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            searchListFragment?.setAdapterList(arrayListOf())
            searchListFragment?.setList(arrayListOf(),
                model.showMedicineList && model.showPharmacyList)

            if (model.showMedicineList && !model.showPharmacyList) {
                model.showMedicineList = false
                CoroutineScope(Dispatchers.IO).launch {
                    searchListFragment?.updateUI()
                    withContext(Dispatchers.Main) {
                        binding.toolbar.navigationIcon = null
                        binding.toolbar.setNavigationOnClickListener(null)
                    }
                }
                return@setNavigationOnClickListener
            }
            model.floatButtonVisibleStatus = View.GONE
            binding.floatingActionButton.visibility =
                model.floatButtonVisibleStatus
            model.showPharmacyList = false
            searchListFragment?.getList(MEDICINE_LIST)?.let {
                        searchListFragment?.setAdapterList(it)
                        searchListFragment?.updateUI()
                }
        }
    }

    private fun initViewModel() {
        model.onPharmacySwitchListener = OnMedicineClickListener { name: String ->
            loadFromTabletka(PharmacyParser, name, true, true){
                model.floatButtonVisibleStatus = View.VISIBLE
                binding.floatingActionButton.visibility = model.floatButtonVisibleStatus
                searchView?.setQuery("", false)
                initBackButton()
            }
        }
        model.onRecipeNameClicked = OnMedicineClickListener { name: String ->
            loadFromTabletka(MedicineParser, name, true, false){
                searchView?.setQuery("", false)
                initBackButton()
            }
        }
    }
    private fun loadFromTabletka(parser: TabletkaParser,
                                 url: String,
                                 medicineFlag: Boolean,
                                 pharmacyFlag: Boolean,
                                 onCompleteListener: () -> Unit){
        searchListFragment?.setAdapterList(arrayListOf())
        searchListFragment?.updateUI()
        model.showMedicineList = medicineFlag
        model.showPharmacyList = pharmacyFlag
        val database = if(medicineFlag && !pharmacyFlag){
            FirebaseMedicineDatabase
        } else if(medicineFlag && pharmacyFlag){
            FirebasePharmacyDatabase
        } else {
            null
        }
        if(database == null) return
        searchListFragment?.loadFromTabletka(parser, database, url,
            medicineFlag && pharmacyFlag,
            onCompleteListener)
    }
    private fun initSearchList() {
        searchListFragment =
            getChildFragmentManager().findFragmentById(R.id.search_list_fragment) as SearchListFragment?
        searchListFragment?.onCompanyNameClicked = model.onRecipeNameClicked
        searchListFragment?.onMedicineNameClicked = model.onPharmacySwitchListener
        searchListFragment?.onRecipeNameClicked = model.onPharmacySwitchListener
        searchListFragment?.onNavigationButtonClicked =
            OnNavigationButtonClicked { pointModel: PointModel ->
                val arg = GeoPointsListArgs(arrayListOf())
                arg.geoPointsList.add(pointModel)
                findNavController(binding.root).navigate(
                    SearchFragmentDirections.showMapFragment(arg)
                )
            }
    }

    private fun initMenus() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.app_bar_filter) {
                val geoPoint = getUserLocation()
                findNavController(binding.root).navigate(
                    SearchFragmentDirections.showListFilterDialog(
                        model.choosedList,
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
            if (model.showMedicineList && model.showPharmacyList) {
                searchListFragment?.getList(PHARMACY_LIST).let {
                    if (it == null) return@let
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
        if (model.showMedicineList || model.showPharmacyList)
            initBackButton()
        getParentFragmentManager().setFragmentResultListener(
            ListFilterDialogFragment.LIST_SETTINGS_KEY_ADD, getViewLifecycleOwner()
        ) { _: String?, result: Bundle ->
            listSettings.sortMask = result.getInt("sortMask")
            listSettings.minPrice = result.getDouble("minPrice")
            listSettings.maxPrice = result.getDouble("maxPrice")
            val list = searchListFragment?.getList(
                model.showPharmacyList &&
                     model.showMedicineList
            )
            list?.let {
                val lst = listSettings.sort(listSettings.filter(it, model.choosedList), model.choosedList)
                searchListFragment?.setAdapterList(lst)
                searchListFragment?.updateUI()
            }
        }
    }
    companion object ActiveList{
        const val MEDICINE_LIST = false
        const val PHARMACY_LIST = true
    }
}