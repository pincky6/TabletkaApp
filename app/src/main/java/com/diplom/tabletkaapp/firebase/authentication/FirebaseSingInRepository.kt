package com.diplom.tabletkaapp.firebase.authentication

import android.widget.Toast
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentLoginBinding
import com.diplom.tabletkaapp.ui.authentication.login.LoginFragmentDirections
import com.diplom.tabletkaapp.util.EditorsUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

object FirebaseSingInRepository {
    private var _auth: FirebaseAuth? = null
    val auth get() = _auth!!

    init {
        _auth = FirebaseAuth.getInstance()
    }
    fun checkUserExist(): Boolean {
        return auth.currentUser != null
    }

    fun isVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }
    fun signInWithGmailAndPassword(
        binding: FragmentLoginBinding,
        gmail: String,
        password: String
    ) {
        auth.signInWithEmailAndPassword(gmail, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    if (!isVerified()) {
                        Toast.makeText(
                            binding.root.context,
                            "Check your gmail",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendVerification(binding)
                    } else {
                        binding.root.findNavController().navigate(
                            LoginFragmentDirections.showContentFragment()
                        )
                    }
                } else {
                    EditorsUtil.setErrorState(binding.passwordEditText)
                    Toast.makeText(
                        binding.root.context,
                        "Something was wrong. Try sign up latter or check gmail and password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener { e: Exception ->
                Toast.makeText(
                    binding.root.context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun sendVerification(binding: FragmentLoginBinding) {
        auth.getCurrentUser()?.sendEmailVerification()
            ?.addOnCompleteListener { task: Task<Void?>? ->
                auth.getCurrentUser()?.sendEmailVerification()!!.addOnCompleteListener { task1: Task<Void?> ->
                    if (task1.isSuccessful) {
                        binding.getRoot().findNavController().navigate(
                            LoginFragmentDirections.showContentFragment()
                        )
                    } else {
                        EditorsUtil.setErrorState(binding.passwordEditText)
                        Toast.makeText(
                            binding.root.context,
                            "Something was wrong. Try sign up latter or check gmail and password",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }.addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        binding.root.context,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}