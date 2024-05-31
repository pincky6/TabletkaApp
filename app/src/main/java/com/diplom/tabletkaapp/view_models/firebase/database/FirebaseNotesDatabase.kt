package com.diplom.tabletkaapp.view_models.firebase.database

import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.firebase.database.TabletkaDatabase
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import models.Hospital

object FirebaseNotesDatabase: TabletkaDatabase {
    val notesDatabase: DatabaseReference
        get() {
            val userId: String = FirebaseAuth.getInstance().currentUser?.email?.replace('.', '-') ?: ""
            return FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes")
        }
    override fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    ){
        notesDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(pharmacy in snapshot.children){
                    pharmacy.getValue<Note>()?.let { list.add(it as Note) }
                }
                onCompleteListener.complete(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onReadCancelled.cancel()
            }
        })
    }

    override fun add(model: AbstractModel, requestId: Long, regionId: Int, query: String) {

    }

    override fun add(model: AbstractModel) {
        val pharmacy = model as Note
        notesDatabase.child(model.id
        ).setValue(pharmacy)
    }
    fun add(model: AbstractModel, onUpdateListener: (()->Unit)?) {
        val pharmacy = model as Note
        notesDatabase.child(model.id
        ).setValue(pharmacy).addOnSuccessListener{
            onUpdateListener?.invoke()
        }
    }

    override fun delete(model: AbstractModel, requestId: Long, regionId: Int, query: String) {
    }

    override fun delete(model: AbstractModel) {
        notesDatabase.child(model.id ).removeValue()

    }
    fun delete(model: AbstractModel, onUpdateListener: (()->Unit)?){
        notesDatabase.child(model.id ).removeValue().addOnSuccessListener {
            onUpdateListener?.invoke()
        }
    }
    override fun generateKey(): String {
        return notesDatabase.push().key ?: "null_key"
    }
}