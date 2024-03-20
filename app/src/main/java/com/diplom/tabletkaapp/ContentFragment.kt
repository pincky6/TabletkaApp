package com.diplom.tabletkaapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.diplom.tabletkaapp.databinding.FragmentContentBinding

class ContentFragment: Fragment() {
    private var _binding: FragmentContentBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController =  Navigation.findNavController(requireActivity(), R.id.content_navigation)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}