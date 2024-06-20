package com.diplom.tabletkaapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diplom.tabletkaapp.models.cache_data_models.converters.PriceRangeConverter

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
 * @param record_id Идентификатор записи.
 */
@Entity(tableName = "medicine")
data class MedicineEntity(
    @PrimaryKey
    val id: String,
    val wish: Boolean,
    val name: String,
    @ColumnInfo(name = "medicine_reference")
    val medicineReference: String,
    val compound: String,
    @ColumnInfo(name = "compound_reference")
    val compoundReference: String,
    val recipe: String,
    @ColumnInfo(name = "recipe_info")
    val recipeInfo: String,
    @ColumnInfo(name = "company_name")
    val companyName: String,
    @ColumnInfo(name = "company_reference")
    val companyReference: String,
    val country: String,
    @TypeConverters(PriceRangeConverter::class)
    @ColumnInfo(name = "price_range")
    val priceRange: MutableList<Double>,
    @ColumnInfo(name = "hospital_count")
    val hospitalCount: Int,
    @ColumnInfo(name = "record_id")
    val record_id: Long
)
