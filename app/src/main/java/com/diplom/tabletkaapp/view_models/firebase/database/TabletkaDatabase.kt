package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel

open interface TabletkaDatabase {

    fun readAll(
        list: MutableList<AbstractModel>,
        onCompleteListener: OnCompleteListener,
        onReadCancelled: OnReadCancelled
    )

    fun add(model: AbstractModel)
    fun delete(model: AbstractModel)

    fun generateKey(): String
}