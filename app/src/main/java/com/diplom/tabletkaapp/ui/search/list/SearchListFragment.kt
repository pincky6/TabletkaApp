package com.diplom.tabletkaapp.ui.search.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.diplom.tabletkaapp.parser.MedicineParser
import com.diplom.tabletkaapp.ui.search.adapters.MedicineAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchListFragment: Fragment() {
    private var _binding: FragmentListBinding? = null
    val binding get() = _binding!!
    var searchListViewModel: SearchListViewModel = SearchListViewModel()
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
    }
    fun loadMedicineFromName(name: String, onCompleteListener: () -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
                searchListViewModel.setList(MedicineParser.getMedicineListFromUrl(name))
                withContext(Dispatchers.Main) {
                    binding.recyclerView.adapter = searchListViewModel.list.value?.let {
                        MedicineAdapter(it)
                    }
                    updateUI()
                    onCompleteListener()
                }
        }
    }
    fun setAdapterList(list: MutableList<AbstractFirebaseModel>){
        binding.recyclerView.adapter?.let {
            (it as MedicineAdapter).list = arrayListOf()
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