package com.diplom.tabletkaapp.parser

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.util.UrlStrings
import models.Hospital
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

object HospitalParser: ITabletkaHealthParser() {
    override fun parsePageFromName(name: String, regionId: Int, page: Int): MutableList<AbstractModel>{
        val pagedUrl = "${name}${UrlStrings.PAGE_CONDITION}${page}"
        return parseFromName(pagedUrl, regionId)
    }
    override fun parseFromName(name : String, regionId: Int): MutableList<AbstractModel>{
        val doc = Jsoup.connect("${UrlStrings.REQUEST_URL}${name}" +
                "${UrlStrings.REGION_CONDITION}${regionId}").get()
        val table = doc.select("tbody")

        val names = getTooltipInfo(table, pharmacyBodyTableString,"pharm-name", "tooltip-info-header",
            "a"){info, element ->
            element.text()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() } as MutableList<String>
        }
        val hospitalsReference = getTooltipInfo(table, pharmacyBodyTableString,"pharm-name", "tooltip-info-header",
            "a"){ info, element ->
            element.getElementsByTag("a").flatMap {
                listOf(it.attr("href").trim().substring(1))
            } as MutableList<String>
        }
        val hospitalsCoordinates = getHospitalCoordinates(hospitalsReference)
        val addresses = getHospitalInfo(table, "address tooltip-info"){element ->
            listOf(element.getElementsByTag("span").text()) as MutableList<String>
        }
        val phones = getHospitalInfo(table, "phone tooltip-info"){element ->
            element.getElementsByTag("a").flatMap {
                listOf(it.attr("href").substring(4))
            } as MutableList<String>
        }
        val pricesInfo = getHospitalPrice(table)
        val expirationDates = parseExpirationDates(pricesInfo)
        val packageNumber = parsePackageNumber(pricesInfo)
        val prices = parsePrice(pricesInfo)
        val pharmacyList: MutableList<AbstractModel> = arrayListOf()
        for(i in 0 until names.size){
            pharmacyList.add(
                Hospital(
                    i.toString(), false,
                    names[i], hospitalsReference[i],
                    hospitalsCoordinates[i][0], hospitalsCoordinates[i][1],
                    addresses[i], phones[i],
                    expirationDates[i], packageNumber[i], prices[i]
            )
            )
        }
        return pharmacyList
    }
    private fun getHospitalCoordinates(hospitalReference: MutableList<String>): MutableList<MutableList<Double>>{
        val hospitalCoordinates: MutableList<MutableList<Double>> = arrayListOf()
        for(i in 0 until hospitalReference.size) {
            val doc = Jsoup.connect("${UrlStrings.BASIC_URL}${hospitalReference[i]}").get()
            val table = doc.select("body")
            hospitalCoordinates.add(table.flatMap { bodyBase ->
                bodyBase.getElementsByClass("map-wrap").flatMap { wrapper ->
                    wrapper.getElementsByClass("map-inner").flatMap { inner ->
                        inner.getElementsByClass("map").flatMap { map ->
                            listOf(
                                map.attr("data-lat").toDouble(),
                                map.attr("data-lon").toDouble()
                            ) as MutableList<Double>
                        }
                    }
                }
            } as MutableList<Double>)
        }
        return hospitalCoordinates
    }
    private fun getHospitalInfo(table: Elements, contentClass: String,
                                contentGetter: (element: Element)-> MutableList<String>): MutableList<String>{
        return table.flatMap { bodyBase ->
            bodyBase.getElementsByClass(contentClass).flatMap {address ->
                address.getElementsByClass("tooltip-info-header").flatMap { content ->
                    content.getElementsByClass(textWrapString).flatMap { wrapper ->
                        contentGetter(wrapper)
                    }
                }
            }
        } as MutableList<String>
    }
    private fun getHospitalPrice(table: Elements): MutableList<MutableList<MutableList<String>>>{
        return table.flatMap { bodyBase ->
            bodyBase.getElementsByClass("price tooltip-info").flatMap {content ->
                content.getElementsByClass("tooltip-info-body").flatMap { body ->
                    body.getElementsByClass("tooltip-info-table").map { tableInfo ->
                        tableInfo.getElementsByClass("tooltip-info-table-tr").map { tr ->
                            tr.getElementsByClass("tooltip-info-table-td").flatMap {
                                listOf(it.text()) as MutableList<String>
                            }
                        }
                    }
                }
            }
        } as MutableList<MutableList<MutableList<String>>>
    }
    private fun parseExpirationDates(priceInfo: MutableList<MutableList<MutableList<String>>>) : MutableList<MutableList<Date>>{
        val expirationDates: MutableList<MutableList<Date>> = arrayListOf()
        for(i in 0 until priceInfo.size){
            val expirationDatePharm: MutableList<Date> = arrayListOf()
            for(j in 0 until priceInfo[i].size) {
                val dateInfo = priceInfo[i][j][0].split(" ")
                if(dateInfo.isEmpty() || dateInfo.size != 3 || dateInfo[2] == "-"){
                    var date = Date()
                    date.time = 0
                    expirationDatePharm.add(date)
                } else {
                    expirationDatePharm.add(SimpleDateFormat("MM/yyyy").parse(dateInfo[2]))
                }
            }
            expirationDates.add(expirationDatePharm)
        }
        return expirationDates
    }
    private fun parsePackageNumber(priceInfo: MutableList<MutableList<MutableList<String>>>) : MutableList<MutableList<Int>>{
        val packageNumber: MutableList<MutableList<Int>> = arrayListOf()
        for(i in 0 until priceInfo.size){
            val packageNumberPharm: MutableList<Int> = arrayListOf()
            for(j in 0 until priceInfo[i].size) {
                val packages = priceInfo[i][j][1].split(" ")
                if(packages.isEmpty() || packages[0].toIntOrNull() == null) {
                    packageNumberPharm.add(0)
                } else {
                    packageNumberPharm.add(packages[0].toInt())
                }
            }
            packageNumber.add(packageNumberPharm)
        }
        return packageNumber
    }
    private fun parsePrice(priceInfo: MutableList<MutableList<MutableList<String>>>) : MutableList<MutableList<Double>>{
        val pricesList: MutableList<MutableList<Double>> = arrayListOf()
        for(i in 0 until priceInfo.size){
            val prices: MutableList<Double> = arrayListOf()
            for(j in 0 until priceInfo[i].size) {
                val packages = priceInfo[i][j][2].split(" ")
                if(packages.isEmpty() || packages[0].toDoubleOrNull() == null) {
                    prices.add(0.0)
                } else {
                    prices.add(packages[0].toDouble())
                }
            }
            pricesList.add(prices)
        }
        return pricesList
    }
}