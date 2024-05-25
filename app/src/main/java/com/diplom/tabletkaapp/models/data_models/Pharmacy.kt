package models

import com.diplom.tabletkaapp.models.AbstractModel
import java.util.Date

data class Pharmacy(
    override val id: String,
    val name: String, val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    val expirationDates: MutableList<Date>,
    val packagesNumber: MutableList<Int>,
    val prices: MutableList<Double>
): AbstractModel(id)
