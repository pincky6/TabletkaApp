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
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.views.lists.simple_lists.AbstractModelViewModel

abstract class AbstractModelList: Fragment() {
    private var binding_: FragmentListBinding? = null
    val binding get() = binding_!!

    lateinit var model: AbstractModelViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = FragmentListBinding.inflate(inflater, container, false)
        model = AbstractModelViewModel()
        context?.let {
            model.database = Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java,
                "cache4"
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
                model.listFilterViewModel.title = newText
                val list = model.listFilterViewModel.filterByTitle(model.medicineList)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(list)
                updateUI()
                return false
            }
        })
    }


    public fun updateUI(){
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}