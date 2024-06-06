package com.diplom.tabletkaapp.models.data_models

import com.diplom.tabletkaapp.models.AbstractModel
import java.io.Serializable
import java.util.Date

data class HospitalShort(
    override var id: String, override var wish: Boolean,
    override val name: String, val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    val updateTime: String, val openState: String
): AbstractModel(id, wish, name), Serializable
