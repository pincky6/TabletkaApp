package com.diplom.tabletkaapp.firebase.authentication

import android.widget.Toast
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentResetPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

object FirebaseResetPasswordRepository {
    private var _auth: FirebaseAuth? = null
    val auth get() = _auth!!
    fun resetPasswordFromGmail(
        gmail: String,
        onCompleteListener: OnCompleteSignListener?,
        onFailrueSignListener: OnFailrueSignListener?
    ) {
        auth.sendPasswordResetEmail(gmail)
            .addOnCompleteListener { task: Task<Void?> ->
                onCompleteListener?.completeTask(task.isSuccessful)
            }
            .addOnFailureListener { exception ->
                onFailrueSignListener?.failrueTask(exception)
            }
    }
}