package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Medicine

object FirebaseMedicineDatabase: TabletkaDatabase {
    val medicineDatabase: DatabaseReference
        get() {
        val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
        return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medicine")
    }
    override fun readAll(
        list: MutableList<AbstractFirebaseModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        medicineDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(medicine in snapshot.children){
                        medicine?.getValue<Medicine>()?.let { list.add(it) }
                }
                onCompleteListener.complete(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }
    override fun add(model: AbstractFirebaseModel) {
        val medicine = model as Medicine
        medicineDatabase.child(medicine.id).setValue(medicine)
    }
    override fun delete(model: AbstractFirebaseModel){
        medicineDatabase.child(model.id).removeValue()
    }

    override fun generateKey(): String {
        return medicineDatabase.push().key ?: "null_key"
    }
}