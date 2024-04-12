package models

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.google.firebase.database.Exclude
import java.util.Date
import kotlin.math.abs

data class Pharmacy(
    override var id: String,
    override var name: String, val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    val expirationDates: MutableList<Date>,
    val packagesNumber: MutableList<Int>,
    val prices: MutableList<Double>, override var wish: Boolean)
    : AbstractFirebaseModel(id, name, wish){
    constructor(): this("", "", "", 0.0,
        0.0, "", "", arrayListOf(), arrayListOf(),
        arrayListOf(), false
    )
    override fun equals(other: Any?): Boolean {
        if(other == null || other as? Pharmacy == null) return false
        val pharmacy = other as Pharmacy
        return name == pharmacy.name && hospitalReference == pharmacy.hospitalReference &&
                (abs(latitude - pharmacy.latitude) < 0.00001) && (abs(longitude - pharmacy.longitude) < 0.00001) &&
                address == pharmacy.address && phone == pharmacy.phone &&
                expirationDates == pharmacy.expirationDates && packagesNumber == pharmacy.packagesNumber &&
                prices == pharmacy.prices
    }
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "hospitalReference" to hospitalReference,
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address,
            "phone" to phone,
            "expirationDates" to expirationDates,
            "packagesNumber" to packagesNumber,
            "prices" to prices,
            "wish" to wish
        )
    }
}
