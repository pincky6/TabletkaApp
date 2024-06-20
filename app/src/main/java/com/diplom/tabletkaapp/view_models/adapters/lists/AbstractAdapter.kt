package com.diplom.tabletkaapp.view_models.list.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.views.lists.AbstractModelList

/**
 * Абстрактный адаптер , который содержит в себе лист с моделью для firebase
 * и методом, который определяет поведение приложения, если этот адаптер используется для списка желаний
 */
abstract class AbstractAdapter(open var list: MutableList<AbstractModel>?, open val onWishListClicked: ((Boolean)->Unit)?): RecyclerView.Adapter<ViewHolder>() {
    /**
     * @param newList метод по обновлению списка адаптера
     */
    public fun resetList(newList: MutableList<AbstractModel>){
        list = newList
        notifyDataSetChanged()
    }
}