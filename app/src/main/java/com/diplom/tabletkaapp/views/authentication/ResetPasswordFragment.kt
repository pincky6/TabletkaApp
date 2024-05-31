package com.diplom.tabletkaapp.views.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentResetPasswordBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseResetPasswordRepository
import com.diplom.tabletkaapp.firebase.authentication.OnFailrueSignListener
import com.diplom.tabletkaapp.util.EditorsUtil

class ResetPasswordFragment: Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        EditorsUtil.initTextWatchers(binding.gmailTextText)
        initButton()
        initToolbar()
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            findNavController(binding.root).popBackStack()
        }
    }

    private fun initButton() {
        binding.sendButton.setOnClickListener { v: View ->
            if (binding.gmailTextText.getText().toString().isEmpty()) {
                EditorsUtil.setErrorBackground(binding.gmailTextText)
                return@setOnClickListener
            }
            FirebaseResetPasswordRepository.resetPasswordFromGmail(
                binding.gmailTextText.getText().toString(),
                { successful ->
                    if (successful) {
                        Toast.makeText(
                            binding.root.context,
                            getString(R.string.check_gmail),
                            Toast.LENGTH_LONG
                        ).show()
                        binding.getRoot().findNavController().popBackStack()
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            getString(R.string.check_gmail_error),
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
}