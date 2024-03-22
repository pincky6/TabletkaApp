package models

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class Medicine(
    val name: String, val medicineReference: String,
    val compound: String, val compoundReference: String,
    val recipe: String, val recipeInfo: String,
    val companyName: String, val companyReference: String,
    val country: String, val priceRange: MutableList<Double>, val hospitalCount: Int,
    var wish: Boolean
) : AbstractFirebaseModel()