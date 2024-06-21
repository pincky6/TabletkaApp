package com.diplom.tabletkaapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.diplom.tabletkaapp.databinding.FragmentContentBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseSettingsDatabase
import com.diplom.tabletkaapp.util.LocaleHelper
import com.diplom.tabletkaapp.view_models.SettingsViewModel
import com.diplom.tabletkaapp.views.mainmenu.MainMenuFragment
import java.util.Locale

/**
 * Класс отвечающий за навигационную панель приложения
 */
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

    /**
     * Инициализируется перемещение.
     * Если мы перемещаемся в представление, которое не является заметками, настройками, главным меню или меню списком желаний
     * то прячем навигационную панель
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController =  Navigation.findNavController(requireActivity(), R.id.content_navigation)
        NavigationUI.setupWithNavController(binding.navView, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.navigation_main_menu ||
                destination.id == R.id.navigation_wish_list ||
                destination.id == R.id.navigation_profile ||
                destination.id == R.id.navigation_notes) {
                    binding.navView.setVisibility(View.VISIBLE)
                    return@addOnDestinationChangedListener
            }
            binding.navView.setVisibility(View.GONE)
        }

        val navigation = childFragmentManager.findFragmentById(R.id.content_navigation) as NavHostFragment
        Log.d("jio", navigation.toString())
        navigation.childFragmentManager.setFragmentResultListener(
            MainMenuFragment.SWITCH_TO_WISH_LIST,
            viewLifecycleOwner){_, _ ->
            binding.navView.selectedItemId = R.id.navigation_wish_list
        }
//        NavHostFragment{762fdd3} (44a41aaf-e101-49bc-903c-27c0378f7746 id=0x7f08009c)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}