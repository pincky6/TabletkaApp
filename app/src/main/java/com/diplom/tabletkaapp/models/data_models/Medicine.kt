package models

import com.diplom.tabletkaapp.models.AbstractModel


data class Medicine(
    override val id: String, override var wish: Boolean,
    val name: String, val medicineReference: String,
    val compound: String, val compoundReference: String,
    val recipe: String, val recipeInfo: String,
    val companyName: String, val companyReference: String,
    val country: String, val priceRange: MutableList<Double>, val hospitalCount: Int
): AbstractModel(id, wish)