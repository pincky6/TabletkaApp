package com.diplom.tabletkaapp.models.data_models

data class Settings(var themeMode: Int, var notesMode: Int, var languageMode: Int){
    constructor(): this(0, 0, 0)
}