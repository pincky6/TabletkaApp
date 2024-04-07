package com.diplom.tabletkaapp.ui.wishlists.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentWishListsBinding
import com.google.android.material.tabs.TabLayout

class WishListsFragment: Fragment() {
    private val lists = listOf(
        MedicineWishListFragment(),
        PharmacyWishListFragment()
    )

    private var _binding: FragmentWishListsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                parentFragmentManager
                    .beginTransaction().replace(R.id.placeHolder, lists[tab?.position!!])
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }
}