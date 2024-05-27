package models

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.util.UrlStrings


data class Medicine(
    override val id: String,
    override var wish: Boolean,
    val name: String,
    val medicineReference: String,
    val compound: String,
    val compoundReference: String,
    val recipe: String,
    val recipeInfo: String,
    val companyName: String,
    val companyReference: String,
    val country: String,
    val priceRange: MutableList<Double>,
    val hospitalCount: Int
) : AbstractModel(id, wish) {
    override fun toString(): String {
        var medicineInfo = "Имя: " + name + "\n" +
                "Ссылка на лекарство: " + "${UrlStrings.REQUEST_URL}${medicineReference}" + "\n" +
                "Состав: " + compound + "\n" +
                "Ссылка на состав: " + "${UrlStrings.REQUEST_URL}${compoundReference}" + "\n" +
                "Рецепт: " + recipe + "\n" +
                "Информация по рецепту: " + recipeInfo + "\n" +
                "Название компании: " + companyName + "\n" +
                "Ссылка на компанию: " + "${UrlStrings.REQUEST_URL}${companyReference}" + "\n"
        if(priceRange.size == 2){
            medicineInfo += "\t${priceRange[0]}-${priceRange[1]}\n"
        } else if(priceRange.size == 1) {
            medicineInfo += "\t${priceRange[0]}\n"
        } else {
            medicineInfo += "\tНет в продаже\n"
        }
        medicineInfo += "Количество аптек: " + hospitalCount
        return medicineInfo
    }
}