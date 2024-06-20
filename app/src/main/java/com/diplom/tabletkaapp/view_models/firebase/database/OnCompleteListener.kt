package com.diplom.tabletkaapp.firebase.database

import com.diplom.tabletkaapp.models.AbstractModel

/**
 * интерфейс описывающий действие после успешного проведения операции
 */
interface OnCompleteListener {
    /**
     * Метод, который вызывается при завершении задачи
     */
    fun complete(list: MutableList<AbstractModel>)
}