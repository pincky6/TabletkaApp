package com.diplom.tabletkaapp.viewmodel.parser

import com.diplom.tabletkaapp.models.Region
import com.diplom.tabletkaapp.util.UrlStrings
import org.jsoup.Jsoup

/**
 * Класс описывающий парсер регионов
 */
object RegionParser {
    /**
     * Тег для парсинга регионов
     */
    private val regionTag: String = "select-check-list region-select-list"

    /**
     * Метод для парсинга регионов
     * Получаем таблицу спускаемся по тегам и получаем регионы
     */
    fun parseRegion(): MutableList<Region>{
        val doc = Jsoup.connect("${UrlStrings.BASIC_URL}").get()
        val table = doc.select("body")
        return table.flatMap{tableBody ->
            tableBody.getElementsByClass(regionTag).flatMap { selectCheckItems->
                selectCheckItems.getElementsByClass("select-check-item").flatMap {
                    listOf(Region(it.attr("value").trim().toInt(),
                           it.text())) as MutableList<Region>
                } as MutableList<Region>
            }
        } as MutableList<Region>
    }
}