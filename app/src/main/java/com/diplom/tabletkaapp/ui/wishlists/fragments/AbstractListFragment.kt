package com.diplom.tabletkaapp.ui.wishlists.fragments

import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.diplom.tabletkaapp.models.PointModel
import com.diplom.tabletkaapp.ui.search.GeoPointsListArgs
import com.diplom.tabletkaapp.ui.search.adapters.TabletkaAdapter
import com.diplom.tabletkaapp.ui.wishlists.viewmodels.TabletkaListViewModel

abstract class AbstractListFragment: Fragment() {

    var _binding: FragmentListBinding? = null
    val binding get() = _binding!!

    val model = TabletkaListViewModel()
    fun getList(): MutableList<AbstractFirebaseModel> {
        return model.list
    }

    fun setList(list: MutableList<AbstractFirebaseModel>) {
        model.list = list
    }

    fun initRecyclerView(){
        _binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        _binding?.recyclerView?.adapter = TabletkaAdapter(model.list, {}, {}, {},
            { pointModel: PointModel ->
                val arg = GeoPointsListArgs(arrayListOf())
                arg.geoPointsList.add(pointModel)
                Navigation.findNavController(binding.root).navigate(
                    WishListFragmentDirections.showMapFragment(arg)
                )
            })
        setAdapterList(model.list)
        updateUI()
    }
    fun setAdapterList(list: MutableList<AbstractFirebaseModel>){
        _binding?.let {
            (it.recyclerView.adapter as TabletkaAdapter).list = list
        }
    }
    fun updateUI(){
        _binding?.recyclerView?.adapter?.notifyDataSetChanged()
    }
}