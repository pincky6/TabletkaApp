package com.diplom.tabletkaapp.models.data_models

import com.diplom.tabletkaapp.models.AbstractModel
import java.io.Serializable

data class Note(
    override var id: String, override var wish: Boolean,
    override var name: String, var describe: String): AbstractModel(id, wish, name), Serializable {
    constructor(): this("",false, "", "")
}