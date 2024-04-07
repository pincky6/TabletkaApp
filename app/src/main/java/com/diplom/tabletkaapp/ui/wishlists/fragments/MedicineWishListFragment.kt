package com.diplom.tabletkaapp.ui.wishlists.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.diplom.tabletkaapp.ui.wishlists.viewmodels.MedicineWishListViewModel

class MedicineWishListFragment : AbstractListFragment() {

    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun getList(): MutableList<AbstractFirebaseModel> {
        TODO("Not yet implemented")
    }

    override fun setList(list: MutableList<AbstractFirebaseModel>) {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(MedicineWishListViewModel::class.java)

        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}