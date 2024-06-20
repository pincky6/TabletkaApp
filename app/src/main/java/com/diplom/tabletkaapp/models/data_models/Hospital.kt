package models

import com.diplom.tabletkaapp.models.AbstractModel
import java.io.Serializable
import java.util.Date

/**
 * Класс, представляющий сущность "Аптека".
 *
 * @param id Уникальный идентификатор аптеки.
 * @param wish Флаг, указывающий, добавлена ли аптека в список желаний.
 * @param name Название аптеки.
 * @param hospitalReference Ссылка на информацию об аптеке.
 * @param latitude Широта местоположения аптеки.
 * @param longitude Долгота местоположения аптеки.
 * @param address Адрес аптеки.
 * @param phone Телефон аптеки.
 * @param expirationDates Список дат истечения срока годности лекарств в аптеке.
 * @param packagesNumber Список количества упаковок лекарств в аптеке.
 * @param prices Список цен на лекарства в аптеке.
 */
data class Hospital(
    override var id: String, override var wish: Boolean,
    override val name: String, val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    val expirationDates: MutableList<Date>,
    val packagesNumber: MutableList<Int>,
    val prices: MutableList<Double>
): AbstractModel(id, wish, name), Serializable {
    /**
     * Конструктор, который инициализирует данные по умолчанию. Необходим для работы с Firebase
     */
    constructor(): this("", false, "", "", 0.0,
        0.0, "", "", arrayListOf(), arrayListOf(),
        mutableListOf()
    )
}
