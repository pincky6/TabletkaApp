package com.diplom.tabletkaapp.models.cache_data_models.pharmacy_entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diplom.tabletkaapp.models.cache_data_models.converters.ExpirationDatesConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PackagesNumberConverter
import com.diplom.tabletkaapp.models.cache_data_models.converters.PriceRangeConverter
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
 * @param pageId Идентификатор страницы аптеке.
 * @param regionId Идентификатор региона, к которому относится больница.
 * @param medicineId Идентификатор связанного лекарства.
 * @param recordId Идентификатор записи.
 */
@Entity(tableName = "hospitals")
data class
HospitalEntity(
    @PrimaryKey
    val id: String,
    val wish: Boolean,
    val name: String,
    val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    @TypeConverters(ExpirationDatesConverter::class)
    val expirationDates: MutableList<Date>,
    @TypeConverters(PackagesNumberConverter::class)
    val packagesNumber: MutableList<Int>,
    @TypeConverters(PriceRangeConverter::class)
    val prices: MutableList<Double>,
    val pageId: Int,
    val regionId: Int,
    val medicineId: Long,
    val recordId: Long
)