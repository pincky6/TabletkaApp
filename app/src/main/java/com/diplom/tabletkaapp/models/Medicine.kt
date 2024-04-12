package models

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import com.google.firebase.database.Exclude
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class Medicine(
    override var id: String,
    override var name: String, val medicineReference: String,
    val compound: String, val compoundReference: String,
    val recipe: String, val recipeInfo: String,
    val companyName: String, val companyReference: String,
    val country: String, val priceRange: MutableList<Double>, val hospitalCount: Int,
    override var wish: Boolean
) : AbstractFirebaseModel(id, name, wish)
{
    constructor(): this("","", "", "",
        "", "", "", "", "",
        "", arrayListOf(), 0, false
    )

    override fun equals(other: Any?): Boolean {
        if(other == null || other as? Medicine == null) return false
        val medicine = other as Medicine
        return name == medicine.name && medicineReference == medicine.medicineReference &&
               compound == medicine.compound && compoundReference == medicine.compoundReference &&
               recipe == medicine.recipe && recipeInfo == medicine.recipeInfo &&
               companyName == medicine.companyName && companyReference == medicine.companyReference &&
               country == medicine.country && priceRange == medicine.priceRange &&
               hospitalCount == medicine.hospitalCount
    }
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "medicineReference" to medicineReference,
            "compound" to compound,
            "compoundReference" to compoundReference,
            "recipe" to recipe,
            "recipeInfo" to recipeInfo,
            "companyName" to companyName,
            "companyReference" to companyReference,
            "country" to country,
            "priceRange" to priceRange,
            "hospitalCount" to hospitalCount,
            "wish" to wish,
        )
    }
}