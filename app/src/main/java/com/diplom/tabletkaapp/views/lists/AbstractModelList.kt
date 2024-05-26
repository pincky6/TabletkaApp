package com.diplom.tabletkaapp.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.FragmentListBinding

abstract class AbstractModelList: Fragment() {
    private var binding_: FragmentListBinding? = null
    val binding get() = binding_!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = FragmentListBinding.inflate(inflater, container, false)
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
        binding.recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
    protected fun initUpdateButton(update: () -> Unit){
        binding.updateButton.setOnClickListener{
            update()
        }
    }protected fun initFilterButton(filter: () -> Unit){
        binding.filterButton.setOnClickListener{
            filter()
        }
    }

    public fun updateUI(){
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}