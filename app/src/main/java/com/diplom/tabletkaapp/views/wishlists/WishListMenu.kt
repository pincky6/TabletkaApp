package com.diplom.tabletkaapp.views.wishlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentMainMenuBinding
import com.diplom.tabletkaapp.databinding.FragmentWishListMenuBinding

/**
 * Класс главного меню списка желаний
 */
class WishListMenu: Fragment() {
    var _binding: FragmentWishListMenuBinding? = null
    val binding get() = _binding!!

    /**
     * Метод инициализации представления
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishListMenuBinding.inflate(inflater, container, false)
        initButtons()
        return binding.root
    }

    /**
     * Метод по инициализации перехода в список желаний аптек или лекарств
     */
    private fun initButtons(){
        binding.moveToMedicineWishListButton.setOnClickListener {
            findNavController(binding.root).navigate(
                WishListMenuDirections.showMedicineWishList()
            )
        }
        binding.moveToHospitalWishListButton.setOnClickListener {
            findNavController(binding.root).navigate(
                WishListMenuDirections.showHospitalWishList()
            )
        }
    }
}