package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel

interface OnCompleteListener {
    fun complete(list: MutableList<AbstractModel>)
}