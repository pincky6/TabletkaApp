package com.diplom.tabletkaapp.parser

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

abstract class TabletkaParser {
    protected val bodyBaseTableString: String = "tbody-base-tbl"
    protected val pharmacyBodyTableString: String = "tr-border"
    protected val textWrapString: String = "text-wrap"
    protected val tdTag = "td"

    open fun parse(url: String): MutableList<AbstractFirebaseModel> {
        return arrayListOf()
    }
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
}