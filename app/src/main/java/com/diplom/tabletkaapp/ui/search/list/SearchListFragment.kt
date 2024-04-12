package com.diplom.tabletkaapp.ui.search.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.firebase.database.TabletkaDatabase
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.diplom.tabletkaapp.parser.TabletkaParser
import com.diplom.tabletkaapp.ui.search.adapters.TabletkaAdapter
import com.diplom.tabletkaapp.ui.search.listeners.OnMedicineClickListener
import com.diplom.tabletkaapp.ui.search.listeners.OnNavigationButtonClicked
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchListFragment: Fragment() {
    private var _binding: FragmentListBinding? = null
    val binding get() = _binding!!

    private var searchListViewModel: SearchListViewModel = SearchListViewModel()
    var onCompanyNameClicked: OnMedicineClickListener? = null
    var onMedicineNameClicked: OnMedicineClickListener? = null
    var onRecipeNameClicked: OnMedicineClickListener? = null
    var onNavigationButtonClicked: OnNavigationButtonClicked? = null

    fun getList(flag: Boolean): MutableList<AbstractFirebaseModel>? {
        if(!flag){
            return searchListViewModel.medicineList.value
        }
        return searchListViewModel.pharmacyList.value
    }
    fun setList(list: MutableList<AbstractFirebaseModel>, flag: Boolean){
        if(!flag){
            searchListViewModel.setMedicineList(list)
        } else {
            searchListViewModel.setPharmacyList(list)
        }
    }
    fun loadFromTabletka(parser: TabletkaParser, loader: TabletkaDatabase , url: String,
                         flag: Boolean, onCompleteListener: () -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val parsedList = parser.parse(url)
            val loadedList: MutableList<AbstractFirebaseModel> = arrayListOf()
            loader.readAll(loadedList,
                object : OnCompleteListener{
                    override fun complete(list: MutableList<AbstractFirebaseModel>) {
                        for(loadedTabletkaObject in list){
                            val index = parsedList.indexOf(loadedTabletkaObject)
                            if(index >= 0){
                                parsedList[index].id = loadedTabletkaObject.id
                                parsedList[index].wish = true
                            }
                        }
                        binding.recyclerView.adapter =
                            parsedList.let {
                                TabletkaAdapter(
                                    it, onCompanyNameClicked, onMedicineNameClicked,
                                    onRecipeNameClicked, onNavigationButtonClicked
                                )
                            }
                        setList(parsedList, flag)
                        updateUI()
                        onCompleteListener()
                    }
                },
                object : OnReadCancelled {
                override fun cancel() {
                }
            })
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }
    private fun initRecyclerView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        if(searchListViewModel.pharmacyList.value?.isEmpty() == false){
            searchListViewModel.pharmacyList.value?.let {
                if(binding.recyclerView.adapter == null)
                    binding.recyclerView.adapter = TabletkaAdapter(it, onCompanyNameClicked, onMedicineNameClicked,
                        onRecipeNameClicked, onNavigationButtonClicked)
                setAdapterList(it)
                updateUI()
            }
        } else if(searchListViewModel.medicineList.value?.isEmpty() == false){
            searchListViewModel.medicineList.value?.let {
                if(binding.recyclerView.adapter == null)
                    binding.recyclerView.adapter = TabletkaAdapter(it, onCompanyNameClicked, onMedicineNameClicked,
                        onRecipeNameClicked, onNavigationButtonClicked)
                setAdapterList(it)
                updateUI()
            }
        }
    }
    fun setAdapterList(list: MutableList<AbstractFirebaseModel>){
        binding.recyclerView.adapter?.let {
            (it as TabletkaAdapter).list = list
        }
    }
    fun updateUI(){
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}