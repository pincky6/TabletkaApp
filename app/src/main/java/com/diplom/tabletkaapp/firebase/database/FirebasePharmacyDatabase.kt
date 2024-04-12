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
import models.Pharmacy

object FirebasePharmacyDatabase: TabletkaDatabase {
    val pharmacyDatabase: DatabaseReference
        get() {
            val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
            return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("pharmacy")
        }
    override fun readAll(
        list: MutableList<AbstractFirebaseModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        pharmacyDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(pharmacy in snapshot.children){
                    pharmacy.getValue<Pharmacy>()?.let { list.add(it as Pharmacy) }
                }
                onCompleteListener.complete(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }
    override fun add(model: AbstractFirebaseModel) {
        val pharmacy = model as Pharmacy
        pharmacyDatabase.child(pharmacy.id).setValue(pharmacy)
    }
    override fun delete(model: AbstractFirebaseModel){
        pharmacyDatabase.child(model.id).removeValue()
    }
    override fun generateKey(): String {
        return pharmacyDatabase.push().key ?: "null_key"
    }
}