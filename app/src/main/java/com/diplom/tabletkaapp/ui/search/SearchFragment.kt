package com.diplom.tabletkaapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentSearchBinding
import com.diplom.tabletkaapp.models.PointModel
import com.diplom.tabletkaapp.ui.search.filter.Filter
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
    private val filter: Filter = Filter()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchView()
        initSearchList()
        initMapButton();
    }
    private fun initSearchView(){
        searchView = binding.toolbar.findViewById(R.id.app_bar_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(!model.showMedicineList && !model.showPharmacyList) {
                    searchListFragment?.loadMedicineFromName(filter.title){
                        model.showMedicineList = true
                        initBackButton()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter.title = newText
                val list = searchListFragment?.searchListViewModel?.getList(model.showMedicineList &&
                        model.showPharmacyList)
                list?.let {
                    searchListFragment?.setAdapterList(filter.filterByTitle(it))
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
}