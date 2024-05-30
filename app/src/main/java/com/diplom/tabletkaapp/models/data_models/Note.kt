package com.diplom.tabletkaapp.models.data_models

import com.diplom.tabletkaapp.models.AbstractModel

data class Note(
    override val id: String, override var wish: Boolean,
    override val name: String, val describe: String): AbstractModel(id, wish, name) {
}