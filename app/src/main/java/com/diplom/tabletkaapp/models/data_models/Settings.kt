package com.diplom.tabletkaapp.models.data_models

/**
 * Класс описывающий настройки приложения
 * @param themeMode тема приложения(темная или светлая)
 * @param notesMode режим заметки(списком или сеткой)
 * @param languageMode язык приложения(русский, английский или белорусский)
 */
data class Settings(
    var themeMode: Int,
    var notesMode: Int,
    var languageMode: Int
){
    /**
     * Конструктор по умолчанию, который необходим для работы с Firebase
     */
    constructor(): this(0, 0, 2)
}