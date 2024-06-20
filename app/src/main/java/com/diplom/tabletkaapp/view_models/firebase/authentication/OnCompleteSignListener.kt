package com.diplom.tabletkaapp.firebase.authentication

/**
 * интерфейс описывающий действие после успешного проведения операции
 */
fun interface OnCompleteSignListener {
    /**
     * Метод, который вызывается при завершении задачи
     */
    fun completeTask(successful: Boolean)
}