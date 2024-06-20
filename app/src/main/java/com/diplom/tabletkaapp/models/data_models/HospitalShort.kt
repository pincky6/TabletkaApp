package com.diplom.tabletkaapp.models.data_models

import com.diplom.tabletkaapp.models.AbstractModel
import java.io.Serializable
import java.util.Date

/**
 * Класс, представляющий сущность "Аптека", которая содержит
 * не всю информацию о ней, а лишь краткую характеристику.
 *
 * @param id Уникальный идентификатор аптеки.
 * @param wish Флаг, указывающий, добавлена ли аптека в список желаний.
 * @param name Название аптеки.
 * @param hospitalReference Ссылка на информацию об аптеке.
 * @param latitude Широта местоположения аптеки.
 * @param longitude Долгота местоположения аптеки.
 * @param address Адрес аптеки.
 * @param phone Телефон аптеки.
 * @param updateTime Когда была обновлена запись.
 * @param openState открыта или закрыта аптека.
 */
data class HospitalShort(
    override var id: String, override var wish: Boolean,
    override val name: String, val hospitalReference: String,
    val latitude: Double, val longitude: Double,
    val address: String, val phone: String,
    val updateTime: String, val openState: String
): AbstractModel(id, wish, name), Serializable
