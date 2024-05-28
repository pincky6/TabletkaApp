package com.diplom.tabletkaapp.models.cache_data_models.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * ����� ��� ����������� ���� � ������ � ������� � ����(������������ ��� �������� ������ ��� � Room Database)
 */
class ExpirationDatesConverter {

    @TypeConverter
    fun fromDateList(dates: MutableList<Date>): String {
        return dates.joinToString(",") { date -> date.time.toString() }
    }

    @TypeConverter
    fun toDateList(dateString: String): MutableList<Date> {
        return if (dateString.isEmpty()) {
            emptyList<Date>() as MutableList<Date>
        } else {
            dateString.split(",").map { Date(it.toLong()) } as MutableList<Date>
        }
    }
}