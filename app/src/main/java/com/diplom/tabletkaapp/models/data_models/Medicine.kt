package models

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.util.UrlStrings
import java.io.Serializable

/**
 * Класс, представляющий сущность "Лекарство".
 *
 * @param id Уникальный идентификатор лекарства.
 * @param wish Флаг, указывающий, добавлено ли лекарство в список желаний.
 * @param name Название лекарства.
 * @param medicineReference Ссылка на информацию о лекарстве.
 * @param compound Состав лекарства.
 * @param compoundReference Ссылка на информацию о составе лекарства.
 * @param recipe Рецепт лекарства.
 * @param recipeInfo Дополнительная информация о рецепте.
 * @param companyName Название компании-производителя.
 * @param companyReference Ссылка на информацию о компании-производителе.
 * @param country Страна происхождения лекарства.
 * @param priceRange Диапазон цен на лекарство.
 * @param hospitalCount Количество аптек, где есть это лекарство.
 */
data class Medicine(
    override var id: String,
    override var wish: Boolean,
    override val name: String,
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
) : AbstractModel(id, wish, name), Serializable {
    /**
     * Конструктор, который заполняет данные по умолчанию. Необходим для работы с Firebase
     */
    constructor(): this("",false, "", "",
        "", "", "", "", "",
        "", "", mutableListOf(), 0
    )

    /**
     * Метод для перевода данных об аптеке в строку
     */
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