package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import models.Medicine

object FirebaseMedicineDatabase {
    val medicineDatabase: DatabaseReference
        get() {
        val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
        return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medicine")
    }
    fun readAll(
        list: MutableList<AbstractFirebaseModel>,
        onReadCancelled: OnReadCancelled
    ){
        medicineDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(medicine in snapshot.children){
                    medicine?.let { list.add(medicine.getValue() as Medicine) }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }
    fun add(model: AbstractFirebaseModel) {
        medicineDatabase.child(model.name).setValue(model)
    }
    fun delete(model: AbstractFirebaseModel){
        medicineDatabase.child(model.name).removeValue()
    }
}