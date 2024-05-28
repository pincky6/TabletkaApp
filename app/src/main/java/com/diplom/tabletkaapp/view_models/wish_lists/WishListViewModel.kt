package com.diplom.tabletkaapp.view_models.wish_lists

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel

class WishListViewModel: ViewModel() {
    val list: MutableList<AbstractModel> = mutableListOf()
}