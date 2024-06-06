package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Hospital

object FirebaseHospitalDatabase: TabletkaDatabase {
    val pharmacyDatabase: DatabaseReference
        get() {
            val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
            return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("pharmacy")
        }
    override fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        pharmacyDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(pharmacy in snapshot.children){
                    pharmacy.getValue<Hospital>()?.let {
                        list.add(it as Hospital)
                        list.last().id = pharmacy.key!!
                    }
                }
                onCompleteListener.complete(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }
    override fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String) {
        val pharmacy = model as Hospital
        pharmacyDatabase.child(model.id + "-" + requestId +
                "-" + regionId + "-" + query + "-" + "hospital"
        ).setValue(pharmacy)
    }
    override fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String){
        pharmacyDatabase.child(model.id + "-" + requestId +
                "-" + regionId + "-" + query + "-" + "hospital").removeValue()
    }
    override fun generateKey(): String {
        return pharmacyDatabase.push().key ?: "null_key"
    }
}