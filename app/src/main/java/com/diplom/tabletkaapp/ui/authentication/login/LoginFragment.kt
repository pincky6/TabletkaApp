package com.diplom.tabletkaapp.ui.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentLoginBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.util.EditorsUtil

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!
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
    fun initButtons() {
            binding.signInButton.setOnClickListener { v ->
                if (EditorsUtil.checkEditors(binding.gmailEditText, binding.passwordEditText)) {
                    EditorsUtil.setErrorBackground(binding.gmailEditText, binding.passwordEditText)
                    return@setOnClickListener
                }
                FirebaseSingInRepository.signInWithGmailAndPassword(
                    binding, binding.gmailEditText.getText().toString().trim(),
                    binding.passwordEditText.getText().toString().trim()
                )
            }
            binding.signUpButton.setOnClickListener { v ->
//                binding.getRoot().findNavController().navigate(
//                    LoginFragmentDirections.showRegisterFragment()
//                )
            }
//            binding.resetButton.setOnClickListener { v ->
//                binding.getRoot().findNavController().navigate(
//                    LoginFragmentDirections.showRessetPassword()
//            )
        }
    fun initEditTexts(){
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        EditorsUtil.initTextFilters(binding.gmailEditText, binding.passwordEditText)
    }
}