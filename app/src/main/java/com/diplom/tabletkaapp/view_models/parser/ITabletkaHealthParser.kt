package com.diplom.tabletkaapp.parser

import com.diplom.tabletkaapp.models.AbstractModel
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Абстрактный класс парсера информации с tabletka.by
 */
abstract class ITabletkaHealthParser {
    /**
     * Часто использующиеся теги при парсинге
     */
    protected val bodyBaseTableString: String = "tbody-base-tbl"
    protected val pharmacyBodyTableString: String = "tr-border"
    protected val textWrapString: String = "text-wrap"
    protected val tdTag = "td"

    /**
     * Метод, позволяющий получить информацию с html-кода, полученного при запросе с сайта
     */
    protected fun getTooltipInfo(
        table: Elements, bodyBaseString: String,
        contentClass: String, tooltipClass: String,
        info: String, contentGetter: (info: String, element: Element) -> MutableList<String>
    ): MutableList<String>{
        return table.flatMap{tableBody ->
            tableBody.getElementsByClass(bodyBaseString).flatMap { bodyBase->
                bodyBase.getElementsByTag(tdTag).flatMap { td->
                    td.getElementsByClass(contentClass).flatMap {wrapper ->
                        wrapper.getElementsByClass(textWrapString).flatMap { content->
                            content.getElementsByClass(tooltipClass).flatMap {infoTag ->
                                    contentGetter(info, infoTag)
                            }
                        }
                    }
                }
            }
        } as MutableList<String>
    }

    /**
     * Методы обязательный для переопределения в классах-наследниках
     */
    abstract fun parseFromName(name: String, regionId: Int): MutableList<AbstractModel>
    abstract fun parsePageFromName(name: String, regionId: Int, page: Int): MutableList<AbstractModel>
}