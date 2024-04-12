package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractFirebaseModel

open interface TabletkaDatabase {

    fun readAll(
        list: MutableList<AbstractFirebaseModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    )

    fun add(model: AbstractFirebaseModel)
    fun delete(model: AbstractFirebaseModel)

    fun generateKey(): String
}