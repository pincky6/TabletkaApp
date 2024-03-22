package com.diplom.tabletkaapp.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentRegisterBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSignUpRepository
import com.diplom.tabletkaapp.firebase.authentication.OnFailrueSignListener
import com.diplom.tabletkaapp.util.EditorsUtil

class RegisterFragment: Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        initButtons()
        initEditTexts()
        initToolbar()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun initButtons() {
        binding.signUpButton.setOnClickListener { v ->
            if (EditorsUtil.checkEditors(binding.gmailEditText, binding.passwordEditText)) {
                EditorsUtil.setErrorBackground(binding.gmailEditText, binding.passwordEditText)
                return@setOnClickListener
            }
            FirebaseSignUpRepository.createUserWithGmailAndPassword(    binding.gmailEditText.getText().toString().trim(),
                binding.passwordEditText.getText().toString().trim(),
                { successful ->
                    if (successful) {
                        Toast.makeText(context, "Вы зарегестрированы", Toast.LENGTH_SHORT).show()
                        binding.getRoot().findNavController().popBackStack()
                    } else {
                        EditorsUtil.setErrorState(binding.passwordEditText)
                        Toast.makeText(
                            context,
                            "Ошибка. Попытайтесь зарегистрироваться позже или проверьте gmail и пароль",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                object : OnFailrueSignListener {
                    override fun failrueTask(exception: Exception) {
                        Toast.makeText(
                            context,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                        exception.printStackTrace()
                    }
                })
        }
    }
    fun initEditTexts(){
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        EditorsUtil.initTextFilters(binding.gmailEditText, binding.passwordEditText)
    }
    private fun initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}