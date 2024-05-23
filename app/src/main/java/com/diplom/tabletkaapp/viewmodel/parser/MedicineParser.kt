package com.diplom.tabletkaapp.parser

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.util.UrlStrings
import models.Medicine
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.UUID

object MedicineParser: TabletkaParser()  {
    override fun parsePageFromUrl(url: String, regionId: Int, page: Int): MutableList<AbstractModel>{
        val pagedUrl = "${url}${UrlStrings.PAGE_CONDITION}${page}"
        return PharmacyParser.parseFromUrl(pagedUrl, regionId)
    }
    override fun parseFromUrl(url: String, regionId: Int): MutableList<AbstractModel>{
        val doc = Jsoup.connect("${UrlStrings.BASIC_URL}${url}" +
                "${UrlStrings.REGION_CONDITION}${regionId}").get()
        val table = doc.select("tbody")

        val names = getTooltipInfo(table, bodyBaseTableString, "name tooltip-info", "tooltip-info-header",
            "a") { info, element ->
            element.getElementsByTag(info).text()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() } as MutableList<String>
        }
        val medicinesReference = getTooltipInfo(table, bodyBaseTableString,"name tooltip-info", "tooltip-info-header",
            "a"){ info, element ->
            element.getElementsByTag("a").flatMap {
                listOf(it.attr("href").trim().substring(1))
            } as MutableList<String>
        }
        val compounds = getTooltipInfo(table, bodyBaseTableString, "name tooltip-info", "capture",
            "a"){ info, element ->
            element.getElementsByTag(info).text()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() } as MutableList<String>
        }
        val compoundReference = getTooltipInfo(table, bodyBaseTableString, "name tooltip-info", "capture",
            "a"){ info, element ->
            element.getElementsByTag(info).flatMap {
                listOf(it.attr("href").trim().substring(1))
            } as MutableList<String>
        }
        val recipes = getTooltipInfo(table, bodyBaseTableString, "form tooltip-info", "tooltip-info-header",
            "a") { info, element ->
            element.getElementsByTag(info).text()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() } as MutableList<String>
        }
        val recipesInfo = getTooltipInfo(table, bodyBaseTableString,"form tooltip-info", "capture",
            "span"){ info, element ->
            listOf( element.text().trim()) as MutableList<String>
        }
        val companies = getTooltipInfo(table, bodyBaseTableString, "produce tooltip-info", "tooltip-info-header",
            "a") { info, element ->
            element.getElementsByTag(info).text()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() } as MutableList<String>
        }
        val companiesReference = getTooltipInfo(table, bodyBaseTableString, "produce tooltip-info", "tooltip-info-header",
            "a"){ info, element ->
            element.getElementsByTag("a").flatMap {
                listOf(it.attr("href").trim().substring(1))
            } as MutableList<String>
        }
        val countries = getTooltipInfo(table, bodyBaseTableString, "produce tooltip-info", "capture",
            "span"){ info, element ->
            listOf(element.text().trim()) as MutableList<String>
        }
        val prices = getMedicinePriceInfo(table, "price-value")
        val hospitalsCount = getHospitalsCount(table, "price", "capture")
        val size = names.size
        var medicineList: MutableList<AbstractModel> = arrayListOf()
        for(i in 0 until size){
            medicineList.add(
                Medicine(
                    UUID.randomUUID().toString(),
                    names[i], medicinesReference[i],
                    compounds[i], compoundReference[i],
                    recipes[i], recipesInfo[i],
                    companies[i], companiesReference[i],
                    countries[i], prices[i], hospitalsCount[i])
            )
        }
        return medicineList
    }
    private fun getMedicinePriceInfo(
        table: Elements, priceClass: String
    ): MutableList<MutableList<Double>>{
        return table.flatMap { tableBody ->
            tableBody.getElementsByClass(bodyBaseTableString).flatMap { bodyBase ->
                bodyBase.getElementsByTag(tdTag).flatMap { td ->
                    td.getElementsByClass(priceClass).map { priceClass->
                        parsePrice(priceClass.text())
                    }
                }
            }
        } as MutableList<MutableList<Double>>
    }
    private fun parsePrice(price: String): MutableList<Double> {
        var list: MutableList<Double> = arrayListOf()
        if(price.contains(" ... ")){
            val priceList = price.split(" ")
            list = listOf(priceList.get(0).toDouble(), priceList.get(2).toDouble()).toMutableList()
            return list
        }
        val splitInfo = price.split(" ")
        if(splitInfo.isNotEmpty() && splitInfo.get(0).toDoubleOrNull() != null){
            list = listOf(splitInfo.get(0).toDouble()).toMutableList()
        }
        return list
    }
    private fun getHospitalsCount(
        table: Elements, contentClass: String, priceClass: String
    ): MutableList<Int>{
        return table.flatMap { tableBody ->
            tableBody.getElementsByClass(bodyBaseTableString).flatMap { bodyBase ->
                bodyBase.getElementsByTag(tdTag).flatMap { td ->
                    td.getElementsByClass(contentClass).flatMap {
                        td.getElementsByClass(priceClass).flatMap { priceClass ->
                            parseHospitalCount(priceClass.text())
                        }
                    }
                }
            }
        } as MutableList<Int>
    }
    private fun parseHospitalCount(string: String): MutableList<Int>{
        val hospitalInfo = string.split(" ")
        var hospitalCount: Int = 0
        if(hospitalInfo.size == 3){
            hospitalCount = hospitalInfo[1].toInt()
        }
        return listOf(hospitalCount).toMutableList()
    }
}