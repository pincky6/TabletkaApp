package com.diplom.tabletkaapp.views.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentLoginBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.authentication.OnFailrueSignListener
import com.diplom.tabletkaapp.util.EditorsUtil

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!
    override fun onStart() {
        super.onStart()
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        if (FirebaseSingInRepository.isUserExist && FirebaseSingInRepository.isVerified) {
            findNavController(binding.root).navigate(
                LoginFragmentDirections.showContentFragment()
            )
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        initButtons()
        initEditTexts()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initButtons() {
        binding.signInButton.setOnClickListener { v ->
            if (EditorsUtil.checkEditors(binding.gmailEditText, binding.passwordEditText)) {
                EditorsUtil.setErrorBackground(binding.gmailEditText, binding.passwordEditText)
                return@setOnClickListener
            }
            FirebaseSingInRepository.signInWithGmailAndPassword(
                binding.gmailEditText.getText().toString().trim(),
                binding.passwordEditText.getText().toString().trim(),
                { successful: Boolean ->
                    if (!successful) {
                        EditorsUtil.setErrorState(binding.passwordEditText)
                        Toast.makeText(
                            binding.root.context,
                            "Ошибка. Зайдите на почту позже или проверьте почту и пароль",
                            Toast.LENGTH_LONG
                        ).show()
                        return@signInWithGmailAndPassword
                    }
                    if (!FirebaseSingInRepository.isVerified) {
                        Toast.makeText(
                            binding.root.context,
                            "Проверьте свою почту",
                            Toast.LENGTH_LONG
                        ).show()
                        sendVerificationMessage()
                        return@signInWithGmailAndPassword
                    }
                    findNavController(binding.root).navigate(
                        LoginFragmentDirections.showContentFragment()
                    )
                },
                object : OnFailrueSignListener {
                    override fun failrueTask(exception: Exception) {
                        Toast.makeText(
                            binding.root.context,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        binding.signUpButton.setOnClickListener { v ->
            findNavController(binding.root).navigate(
                LoginFragmentDirections.showRegisterFragment()
            )
        }
        binding.resetPasswordButton.setOnClickListener { v ->
            findNavController(binding.root).navigate(
                LoginFragmentDirections.showResetPasswordFragment()
            )
        }
    }
    private fun initEditTexts(){
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        EditorsUtil.initTextFilters(binding.gmailEditText, binding.passwordEditText)
    }

    private fun sendVerificationMessage(){
        FirebaseSingInRepository.sendVerification({ successful: Boolean ->
            if (successful) {
                findNavController(binding.root).navigate(
                    LoginFragmentDirections.showContentFragment()
                )
                return@sendVerification
            }
                EditorsUtil.setErrorState(binding.passwordEditText)
                Toast.makeText(
                    binding.root.context,
                    "Произошла ошибка. Попытайтесь войти в аккаунт позже или проверьте gmail и почту",
                    Toast.LENGTH_LONG
                ).show()
        },
            object : OnFailrueSignListener {
                override fun failrueTask(exception: Exception) {
                    Toast.makeText(
                        binding.root.context,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}

