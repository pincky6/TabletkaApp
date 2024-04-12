package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractFirebaseModel

interface OnCompleteListener {
    fun complete(list: MutableList<AbstractFirebaseModel>)
}