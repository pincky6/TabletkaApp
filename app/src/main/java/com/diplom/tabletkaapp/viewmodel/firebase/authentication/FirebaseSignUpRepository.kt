package com.diplom.tabletkaapp.firebase.authentication

import android.content.Context
import android.widget.Toast
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentRegisterBinding
import com.diplom.tabletkaapp.util.EditorsUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

object FirebaseSignUpRepository {
    private var _auth: FirebaseAuth? = null
    val auth get() = _auth!!

    init {
        _auth = FirebaseAuth.getInstance()
    }
    fun createUserWithGmailAndPassword(
        gmail: String,
        password: String,
        onCompleteListener: OnCompleteSignListener?,
        onFailrueSignListener: OnFailrueSignListener?
    ) {
        auth.createUserWithEmailAndPassword(gmail, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                onCompleteListener?.completeTask(task.isSuccessful)
            }.addOnFailureListener { e: Exception ->
                onFailrueSignListener?.failrueTask(e)
            }
    }
}