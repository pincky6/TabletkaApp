package com.diplom.tabletkaapp.view_models.list.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.views.lists.AbstractModelList

abstract class AbstractAdapter(open var list: MutableList<AbstractModel>?, open val onWishListClicked: ((Boolean)->Unit)?): RecyclerView.Adapter<ViewHolder>() {
    public fun resetList(newList: MutableList<AbstractModel>){
        list = newList
        notifyDataSetChanged()
    }
}