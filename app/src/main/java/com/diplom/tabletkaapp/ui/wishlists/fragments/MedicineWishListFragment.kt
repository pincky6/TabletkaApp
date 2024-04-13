package com.diplom.tabletkaapp.ui.wishlists.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractFirebaseModel

class MedicineWishListFragment : AbstractListFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        model.database = FirebaseMedicineDatabase
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.list.clear()
        model.loadFromDatabase(object : OnCompleteListener{
            override fun complete(list: MutableList<AbstractFirebaseModel>) {
                initRecyclerView()
            }
        },
            object : OnReadCancelled{
                override fun cancel() {

                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}