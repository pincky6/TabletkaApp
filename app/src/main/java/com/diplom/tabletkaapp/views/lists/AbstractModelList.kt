package com.diplom.tabletkaapp.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterDialogFragment
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.views.lists.simple_lists.AbstractModelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.Medicine

abstract class AbstractModelList: Fragment() {
    private var binding_: FragmentListBinding? = null
    val binding get() = binding_!!

    var model: AbstractModelViewModel = AbstractModelViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = FragmentListBinding.inflate(inflater, container, false)
        context?.let {
            model.database = Room.databaseBuilder(
                requireContext().applicationContext,
                AppDatabase::class.java,
                DatabaseInfo.DATABASE_NAME
            ).build()

        }
        initBackButton()
        initSearchView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }
    protected fun initRecyclerView(adapter: RecyclerView.Adapter<ViewHolder>){
        if (binding_ == null) return
        binding.recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
    protected fun initUpdateButton(update: () -> Unit){
        if (binding_ == null) return
        binding.updateButton.setOnClickListener{
            update()
        }
    }protected fun initFilterButton(filter: () -> Unit){
        if (binding_ == null) return
        binding.filterButton.setOnClickListener{
            filter()
        }
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
                val list = model.listFilter.filterByTitle(model.modelList)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(list)
                updateUI()
                return false
            }
        })
    }
    protected fun initGetFilter(listType: Boolean) {
        getParentFragmentManager().setFragmentResultListener(
            ListFilterDialogFragment.LIST_SETTINGS_KEY_ADD, getViewLifecycleOwner()
        ) { _: String?, result: Bundle ->
            model.listFilter.sortMask = result.getInt("sortMask")
            model.listFilter.minPrice = result.getDouble("minPrice")
            model.listFilter.maxPrice = result.getDouble("maxPrice")
            val list = model.listFilter.filter(model.modelList, listType)
            list?.let {
                val lst = model.listFilter.sort(model.listFilter.filter(it, listType), listType)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(lst)
                updateUI()
            }
        }
    }

    protected suspend fun initRecyclerViewWithMainContext(adapter: AbstractAdapter, medicineList: MutableList<AbstractModel>){
        withContext(Dispatchers.Main){
            model.modelList = medicineList
            adapter.resetList(medicineList)
            initRecyclerView(adapter)
        }
    }

    public fun updateUI(){
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}