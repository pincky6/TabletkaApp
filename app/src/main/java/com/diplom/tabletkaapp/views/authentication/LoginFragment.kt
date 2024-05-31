package com.diplom.tabletkaapp.views.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentLoginBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.authentication.OnFailrueSignListener
import com.diplom.tabletkaapp.util.EditorsUtil
import com.google.firebase.auth.FirebaseAuth

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!
    override fun onStart() {
        super.onStart()
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        if (FirebaseSingInRepository.isUserExist && FirebaseSingInRepository.isVerified) {
            FirebaseAuth.getInstance().currentUser?.let {
                it.email?.let {
                    binding.gmailEditText.setText(it)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        initButtons()
        initEditTexts()
        initToolbar()
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
                            getString(R.string.check_gmail_error),
                            Toast.LENGTH_LONG
                        ).show()
                        return@signInWithGmailAndPassword
                    }
                    if (!FirebaseSingInRepository.isVerified) {
                        Toast.makeText(
                            binding.root.context,
                            R.string.check_gmail,
                            Toast.LENGTH_LONG
                        ).show()
                        sendVerificationMessage()
                        return@signInWithGmailAndPassword
                    }
                    else
                    {
                        findNavController(binding.root).popBackStack()
                    }
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
    private fun initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
    private fun initEditTexts(){
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        EditorsUtil.initTextFilters(binding.gmailEditText, binding.passwordEditText)
    }

    private fun sendVerificationMessage(){
        FirebaseSingInRepository.sendVerification({ successful: Boolean ->
            if (successful) {
                findNavController(binding.root).popBackStack()
                return@sendVerification
            }
                EditorsUtil.setErrorState(binding.passwordEditText)
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.check_gmail_error),
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

