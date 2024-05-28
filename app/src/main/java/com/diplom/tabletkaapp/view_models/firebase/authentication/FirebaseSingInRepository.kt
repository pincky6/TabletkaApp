package com.diplom.tabletkaapp.firebase.authentication

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
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
        gmail: String,
        password: String,
        onCompleteListener: OnCompleteSignListener?,
        onFailrueSignListener: OnFailrueSignListener?
    ) {
        auth.signInWithEmailAndPassword(gmail, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                onCompleteListener?.completeTask(task.isSuccessful)
            }.addOnFailureListener { e: Exception ->
                onFailrueSignListener?.failrueTask(e)
            }
    }

    fun sendVerification(
        onCompleteListener: OnCompleteSignListener?,
        onFailrueSignListener: OnFailrueSignListener?
    ) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                auth.currentUser?.sendEmailVerification()!!.addOnCompleteListener { task: Task<Void?> ->
                    onCompleteListener?.completeTask(task.isSuccessful)
                }.addOnFailureListener { e: Exception ->
                    onFailrueSignListener?.failrueTask(e)
                }
            }
    }

    fun checkUserExistWithWarningDialog(context: Context): Boolean{
        if(!isUserExist){
            AlertDialog.Builder(context)
                .setTitle("¬ы не зарегестрированы")
                .setMessage("≈сли вы хотите добавл€ть что-то в избранное," +
                        " то вы должны зарегестрироватьс€")
                .setPositiveButton("OK") { _, _ ->
                }
                .create().show()
        }
        return isUserExist
    }
}