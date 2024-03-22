package com.diplom.tabletkaapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentSearchBinding
import com.diplom.tabletkaapp.ui.search.filter.Filter
import com.diplom.tabletkaapp.ui.search.list.SearchListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    val binding get() = _binding!!
    private var searchView: SearchView? = null
    private val filter: Filter = Filter()
    private var searchListFragment: SearchListFragment? = null
    var showMedicineList: Boolean = false
    var showPharmacyList: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchListFragment =
            getChildFragmentManager().findFragmentById(R.id.search_list_fragment) as SearchListFragment?
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchView()
    }
    private fun initSearchView(){
        searchView = binding.toolbar.findViewById(R.id.app_bar_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(!showMedicineList && !showPharmacyList) {
                    searchListFragment?.loadMedicineFromName(filter.title){
                        showMedicineList = true
                        initMedicineBackButton()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
               filter.title = newText
              //  resetAdapterList(filter.filterByTitle(taskListViewModel.taskList))
                return false
            }
        })
    }
    private fun initMedicineBackButton(){
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            showMedicineList = false
            CoroutineScope(Dispatchers.IO).launch {
                searchListFragment?.setAdapterList(arrayListOf())
                searchListFragment?.searchListViewModel?.setList(arrayListOf())
                searchListFragment?.updateUI()
                withContext(Dispatchers.Main){
                    binding.toolbar.navigationIcon = null
                    binding.toolbar.setNavigationOnClickListener(null)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}