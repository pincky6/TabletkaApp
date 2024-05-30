package com.diplom.tabletkaapp.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentSettingsBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseSettingsDatabase
import com.diplom.tabletkaapp.util.UrlStrings
import com.diplom.tabletkaapp.view_models.SettingsViewModel
import com.diplom.tabletkaapp.views.mainmenu.MainMenuFragmentDirections
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment: Fragment() {
    var binding_: FragmentSettingsBinding? = null
    val binding get() = binding_!!
    lateinit var model: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = SettingsViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentSettingsBinding.inflate(inflater, container, false)
        if(FirebaseSingInRepository.isUserExist){
            binding.profileImage.visibility = View.VISIBLE
            binding.profileText.text = FirebaseAuth.getInstance().currentUser?.email.toString()
        } else {
            binding.profileImage.visibility = View.GONE
            binding.profileText.text = "Вы не зарегестрированы"
        }
        FirebaseSettingsDatabase.readAll(model){
            initThemeModeSpinner()
            initNoteModeSpinner()
            initLoginButton()
            initExitButton()
            initResetPassword()
            initDeleteButton()
            initMoveToSiteButton()
        }
        return binding.root
    }

    private fun initLoginButton(){
        binding.enterButton.setOnClickListener{
            findNavController(binding.root).navigate(
                SettingsFragmentDirections.actionNavigationProfileToLoginFragment()
            )
        }
    }

    private fun initMoveToSiteButton(){
        binding.moveToSiteButton.setOnClickListener {
            val url = UrlStrings.SITE_REFERENCE
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun initResetPassword(){
        binding.resetPasswordButton.setOnClickListener {
            findNavController(binding.root).navigate(
                SettingsFragmentDirections.actionNavigationProfileToResetPasswordFragment()
            )
        }
    }

    private fun initExitButton(){
        binding.exitButton.setOnClickListener {
            FirebaseSingInRepository.signOut()
            Toast.makeText(context, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initDeleteButton(){
        binding.deleteButton.setOnClickListener {
            FirebaseSingInRepository.deleteAccount{
                Toast.makeText(context, "Ваш аккаунт был удален", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun initNoteModeSpinner(){
        val noteModeAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                arrayOf("Сеткой", "Списком")
            )
        binding.noteModeSpinner.adapter = noteModeAdapter
        binding.noteModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding_?.let {
                    view?.let {
                        if (parent.adapter.getItem(position).toString() == "Списком") {
                            model.settings.notesMode = 0
                        } else if (parent.adapter.getItem(position).toString() == "Сетка") {
                            model.settings.notesMode = 1
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            FirebaseSettingsDatabase.add(model.settings)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.noteModeSpinner.setSelection(model.settings.notesMode)
    }
    private fun initThemeModeSpinner() {
        val themeAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                arrayOf("Светлая Тема", "Темная Тема")
            )
        binding.themeSpinner.setAdapter(themeAdapter)
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding_?.let {
                    view?.let {
                        if (parent.adapter.getItem(position).toString() == "Светлая Тема") {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            model.settings.themeMode = 0
                        } else if (parent.adapter.getItem(position).toString() == "Темная Тема") {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            model.settings.themeMode = 1
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            FirebaseSettingsDatabase.add(model.settings)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.themeSpinner.setSelection(model.settings.themeMode)
    }
}