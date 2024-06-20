package com.diplom.tabletkaapp.firebase.database

/**
 * Интерфейс слушателя окончания чтения
 */
interface OnReadCancelled {
    /**
     * метод окончания чтения
     */
    fun cancel()
}