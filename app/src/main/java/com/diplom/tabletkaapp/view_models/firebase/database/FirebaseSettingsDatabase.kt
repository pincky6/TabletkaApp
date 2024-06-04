package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Settings
import com.diplom.tabletkaapp.view_models.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Hospital
import models.Medicine

object FirebaseSettingsDatabase {
    val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
    fun getDatabase(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference().child("users").child(userId)
            .child("settings")
    }
    fun add(settings: Settings) {
        getDatabase().child("0").setValue(settings)
    }
    fun readAll(
        model: SettingsViewModel,
        initFunction: ((Settings)->Unit)?,
        initFunctionCancelled:  (()->Unit)
    ){
        getDatabase().addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(settings in snapshot.children){
                    settings.getValue<Settings>()?.let {
                        model.settings = it
                    }
                }
                initFunction?.invoke(model.settings)
            }
            override fun onCancelled(error: DatabaseError) {
                initFunctionCancelled.invoke()
            }
        })
    }
}