package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentHospitalListBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.ui.search.filter.ListFilterDialogFragment
import com.diplom.tabletkaapp.util.CacheHospitalConverter
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.adapters.lists.HospitalRegionAdapter
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.view_models.list.adapters.HospitalAdapter
import com.diplom.tabletkaapp.view_models.lists.HospitalRegionListViewModel
import com.diplom.tabletkaapp.viewmodel.adapters.mainmenu.RegionAdapter
import com.diplom.tabletkaapp.viewmodel.parser.RegionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Hospital
import org.osmdroid.util.GeoPoint

class HospitalRegionFragment: Fragment() {
    var binding_: FragmentHospitalListBinding? = null
    val binding get() = binding_!!
    val model: HospitalRegionListViewModel = HospitalRegionListViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentHospitalListBinding.inflate(inflater, container, false)
        model.database = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java,
            DatabaseInfo.DATABASE_NAME
        ).build()
        initGetFilter()
        initSpinner()
        initBackButton()
        initSearchView()
        initFloatingButton()
        loadData()
        return binding.root
    }
    /**
     * Инициализация кнопки выхода из окна
     */
    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            model.database?.let {
                val hospitalList = mutableListOf<AbstractModel>()
                    hospitalList.addAll(
                        HospitalParser.parseFromRegionAndPage(
                            model.regionId,
                            1
                        )
                    )
                model.list = hospitalList
                initRecyclerView(HospitalRegionAdapter(hospitalList, 1, model.regionId, null))
            }
        }
    }

    private fun initSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(binding_ == null || binding.recyclerView.adapter == null) return false
                model.listFilter.title = newText
                val list = model.listFilter.filterByTitle(model.list)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(list)
                (binding.recyclerView.adapter as AbstractAdapter).notifyDataSetChanged()
                return false
            }
        })
    }
    protected fun initGetFilter() {
        getParentFragmentManager().setFragmentResultListener(
            ListFilterDialogFragment.LIST_SETTINGS_KEY_ADD, getViewLifecycleOwner()
        ) { _: String?, result: Bundle ->
            model.listFilter.sortMask = result.getInt("sortMask")
            model.listFilter.minPrice = result.getDouble("minPrice")
            model.listFilter.maxPrice = result.getDouble("maxPrice")
            val list = model.listFilter.filter(model.list, true)
            list?.let {
                val lst = model.listFilter.sort(model.listFilter.filter(it, true), true)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(lst)
                (binding.recyclerView.adapter as AbstractAdapter).notifyDataSetChanged()

            }
        }
    }

    private fun initFloatingButton(){
        binding.floatingActionButton.setOnClickListener {
            val geoPointsList = GeoPointsList(model.list.flatMap {
                if(it is Hospital){
                    val hospital = it
                    mutableListOf(GeoPoint(hospital.latitude, hospital.longitude))
                } else {
                    val hospitalShort = it as HospitalShort
                    mutableListOf(GeoPoint(hospitalShort.latitude, hospitalShort.longitude))
                }

            } as MutableList<GeoPoint>)
            findNavController(binding.root).navigate(
                HospitalRegionFragmentDirections.actionHospitalRegionFragmentToMapFragment(geoPointsList, HospitalsList(model.list))
            )
        }
    }

    private fun initSpinner() {
        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val regions = RegionParser.parseRegion()
                val allRegionsString = getString(R.string.all_regions_string)
                val allRegion = regions.find {
                    it.name == allRegionsString
                }
                allRegion?.let {
                    regions.removeAt(regions.indexOf(allRegion))
                    regions.add(0, allRegion)
                }
                withContext(Dispatchers.Main) {
                    if(binding_ == null) return@withContext
                    binding.regionSpinner.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                    binding.regionSpinner.setPadding(0, 0, 50, 0)
                    binding.regionSpinner.setPromptId(R.string.select_region_string)
                    binding.regionSpinner.adapter = RegionAdapter(it, regions)

                    binding.regionSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                model.regionId = regions[position].id
                                loadData()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            }
        }
    }
    private suspend fun initRecyclerView(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>){
        withContext(Dispatchers.Main) {
            if (binding_ == null) return@withContext
            binding.recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = adapter

        }
    }
}