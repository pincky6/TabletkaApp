package com.diplom.tabletkaapp.firebase.authentication

import android.app.AlertDialog
import android.content.Context
import com.diplom.tabletkaapp.view_models.firebase.authentication.OnDeleteAccountListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    fun signOut(){
        auth.signOut()
    }

    fun deleteAccount(onDeleteAccountListener: OnDeleteAccountListener) {
        FirebaseDatabase.getInstance().reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userId = auth.currentUser?.email?.replace('.', '-')
                    userId?.let {
                        for (children in snapshot.children) {
                            val user = children.key
                            if (userId == user) {
                                children.ref.removeValue().addOnCompleteListener { task: Task<Void?>? ->
                                    auth.currentUser!!
                                        .delete()
                                        .addOnCompleteListener { task1: Task<Void?>? -> onDeleteAccountListener.deleteAccountComplete() }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
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