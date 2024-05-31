package com.diplom.tabletkaapp.views.lists.simple_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentHospitalListBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.ui.search.filter.ListFilterDialogFragment
import com.diplom.tabletkaapp.util.CacheHospitalConverter
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
        initGetFilter()
        initSpinner()
        initBackButton()
        initSearchView()
        initFloatingButton()
        CoroutineScope(Dispatchers.IO).launch {
            val hospitalEntities = model.database.hospitalDao().getHospitalsByRegionIdAndMedicineIdAndRecordId(model.regionId, -1,
                -1)
            var maxPage = model.database.hospitalDao().getMaxPage(-1, model.regionId, -1)
            val convertedList = CacheHospitalConverter.fromEntityListToModelList(hospitalEntities)
            initRecyclerView(
                HospitalAdapter(convertedList, model.database, maxPage, "", model.regionId,
                model.list,
                hospitalModel.medicine.id.toLong(), requestId, null), convertedList)
            val hospitalList = mutableListOf<AbstractModel>()
            if(maxPage == 0) maxPage++
            for(i in 0 until maxPage) {
                hospitalList.addAll(
                    HospitalParser.parsePageFromName(
                        hospitalModel.medicine.medicineReference,
                        regionId,
                        i + 1
                    )
                )
            }
            HospitalCacher.validateMedicineDatabase(model.database, regionId, hospitalModel.medicine.id.toLong(), requestId,
                hospitalList, hospitalEntities)
            initRecyclerViewWithMainContext(
                HospitalAdapter(convertedList, model.database, maxPage, query, regionId,
                hospitalModel.medicine,
                hospitalModel.medicine.id.toLong(), requestId, null), hospitalList)
        }
        initRecyclerView()
        return binding.root
    }

    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
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
                val hospital = (it as Hospital)
                mutableListOf(GeoPoint(hospital.latitude, hospital.longitude))
            } as MutableList<GeoPoint>)
            Navigation.findNavController(binding.root).navigate(
                HospitalModelListDirections.actionHospitalModelListToMapFragment(geoPointsList, null)
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
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            }
        }
    }
    private fun initRecyclerView(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>){
        if (binding_ == null) return
        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
}