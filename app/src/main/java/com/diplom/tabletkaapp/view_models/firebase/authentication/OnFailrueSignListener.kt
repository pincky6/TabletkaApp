package com.diplom.tabletkaapp.firebase.authentication

/**
 * Интерфейс описывающий действия после провала операции
 */
interface OnFailrueSignListener {
    /**
     * Метод, который вызывается при не провальном выполнении операции
     */
    fun failrueTask(exception: Exception)
}