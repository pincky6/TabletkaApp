package com.diplom.tabletkaapp.ui.wishlists.fragments

import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.models.AbstractFirebaseModel

abstract class AbstractListFragment: Fragment() {
    abstract fun getList(): MutableList<AbstractFirebaseModel>
    abstract fun setList(list: MutableList<AbstractFirebaseModel>): Unit
}