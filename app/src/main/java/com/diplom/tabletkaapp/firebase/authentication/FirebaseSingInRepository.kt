package com.diplom.tabletkaapp.firebase.authentication

import android.text.BoringLayout
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
    val isVerified: Boolean
        get() = auth.currentUser?.isEmailVerified ?: false
    val isUserExist:Boolean
        get() = auth.currentUser != null
    init {
        _auth = FirebaseAuth.getInstance()
    }
    fun signInWithGmailAndPassword(
        binding: FragmentLoginBinding,
        gmail: String,
        password: String
    ) {
        auth.signInWithEmailAndPassword(gmail, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    if (!isVerified) {
                        Toast.makeText(
                            binding.root.context,
                            "Check your gmail",
                            Toast.LENGTH_LONG
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
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                auth.currentUser?.sendEmailVerification()!!.addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        binding.root.findNavController().navigate(
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
                    e.printStackTrace()
                }
            }
    }
}