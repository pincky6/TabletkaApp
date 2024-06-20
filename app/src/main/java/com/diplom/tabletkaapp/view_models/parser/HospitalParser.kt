package com.diplom.tabletkaapp.parser

import android.util.Log
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.util.UrlStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import models.Hospital
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

/**
 * Класс парсера аптек из tabletka.by
 */
object HospitalParser: ITabletkaHealthParser() {
    /**
     * Метод для получения аптек по странице и региону
     * @param medicineCode код медикамента
     * @param regionId идентификатор региона
     * @param page страница аптек с которой берем информацию
     */
    override fun parsePageFromName(medicineCode: String, regionId: Int, page: Int): MutableList<AbstractModel>{
        val pagedUrl = "${medicineCode}${UrlStrings.PAGE_CONDITION}${page}"
        return parseFromName(pagedUrl, regionId)
    }

    /**
     * Метод для парсинга информации с tabletka.by
     * Получаем все необходимые сведения об аптеках
     * @param medicineCode код медикамента
     * @param regionId идентификатор региона
     */
    override fun parseFromName(medicineCode : String, regionId: Int): MutableList<AbstractModel>{
        val doc = Jsoup.connect("${UrlStrings.BASIC_URL}${medicineCode}" +
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

        return getHospitals(names, hospitalsReference,
                                hospitalsCoordinates,
                                addresses, phones,
                                expirationDates, packageNumber, prices)
    }

    /**
     * Метод для парсинга аптек с краткой информацией о них
     * @param regionId идентификатор региона
     * @param page страница с которой берем инфу
     */
    fun parseFromRegionAndPage(regionId: Int, page: Int): MutableList<AbstractModel>{
        val doc = Jsoup.connect("https://tabletka.by/pharmacies?region=$regionId&page=$page").get()
        val table = doc.select("tbody")
        val names = getTooltipInfo(table, pharmacyBodyTableString,"pharm-name", "text-wrap",
            "a"){info, element ->
            element.text()
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() } as MutableList<String>
        }
        val updatesTime = mutableListOf<String>()
        for (i in names.size - 1 downTo 0) {
            if (i % 2 == 1) {
                updatesTime.add(names.removeAt(i))
            }
        }
        val hospitalsReference = getTooltipInfo(table, pharmacyBodyTableString,"pharm-name",
            "text-wrap", "a"){ info, element ->
            element.getElementsByTag("a").flatMap {
                listOf(it.attr("href").trim().substring(1))
            } as MutableList<String>
        }
        val hospitalsCoordinates = getHospitalCoordinates(hospitalsReference)
        val addresses = getHospitalInfo(table, "address tooltip-info"){element ->
            listOf(element.getElementsByTag("span").text()) as MutableList<String>
        }
        val openStates = mutableListOf<String>()
        for (i in addresses.size - 1 downTo 0) {
            if (i % 2 == 1) {
                openStates.add(addresses.removeAt(i))
            }
        }
        val phones = getHospitalInfo(table, "phone tooltip-info"){element ->
            element.getElementsByTag("a").flatMap {
                listOf(it.attr("href").substring(4))
            } as MutableList<String>
        }

        return getShortHospitalsInfo(names, hospitalsReference,
            hospitalsCoordinates,
            addresses, phones, updatesTime, openStates)
    }

    /**
     * Метод для загрузки информации в список аптек с краткой инфой о них
     * Здесь происходит проверка пустых полей, которые не указаны на сайте. Вместо них вставляем пустую строку
     */
    private fun getShortHospitalsInfo(names: MutableList<String>, hospitalsReference: MutableList<String>,
                                      hospitalCoordinates: MutableList<MutableList<Double>>,
                                      phones: MutableList<String>, addresses: MutableList<String>,
                                      updatesTime: MutableList<String>, openStates: MutableList<String>): MutableList<AbstractModel>{
        val list = mutableListOf<AbstractModel>()
        val size = mutableListOf(names.size, hospitalsReference.size,
            hospitalCoordinates.size, addresses.size,
            phones.size).maxOrNull() ?: 0
        for(i in 0 until size / 2){
            val name = if(names.size <= i) "" else names[i]
            val updateTime = if(updatesTime.size <= i) "" else updatesTime[i]
            val hospitalReference = if(hospitalsReference.size <= i) "" else hospitalsReference[i]
            val hospitalCoordinate = if(hospitalCoordinates.size <= i) mutableListOf() else hospitalCoordinates[i]
            val addressHospital = if(addresses.size <= i) "" else addresses[i]
            val openState = if(openStates.size <= i) "" else openStates[i]
            val phone = if(phones.size <= i) "" else phones[i]
            list.add(
                HospitalShort(
                    i.toString(), false, name,
                    hospitalReference, hospitalCoordinate[0], hospitalCoordinate[1],
                    addressHospital, phone, updateTime, openState
                )
            )
        }
        return list
    }
    /**
     * Метод для загрузки информации в список аптек
     * Здесь происходит проверка пустых полей, которые не указаны на сайте. Вместо них вставляем пустую строку
     */
    private fun getHospitals(names: MutableList<String>, hospitalsReference: MutableList<String>,
                             hospitalCoordinates: MutableList<MutableList<Double>>, addresses: MutableList<String>,
                             phones: MutableList<String>, expirationDates: MutableList<MutableList<Date>>,
                             packagesNumber: MutableList<MutableList<Int>>, prices: MutableList<MutableList<Double>>) : MutableList<AbstractModel>
    {
        val hospitals = mutableListOf<AbstractModel>()
        val size = mutableListOf(names.size, hospitalsReference.size,
                                 hospitalCoordinates.size, addresses.size,
                                 phones.size, expirationDates.size,
                                 packagesNumber.size, prices.size,
                                 prices.size).maxOrNull() ?: 0
        for(i in 0 until size){
            val name = if(names.size <= i) "" else names[i]
            val hospitalReference = if(hospitalsReference.size <= i) "" else hospitalsReference[i]
            val hospitalCoordinate = if(hospitalCoordinates.size <= i) mutableListOf() else hospitalCoordinates[i]
            val addressHospital = if(addresses.size <= i) "" else addresses[i]
            val phone = if(phones.size <= i) "" else phones[i]
            val expirationDate = if(expirationDates.size <= i) mutableListOf() else expirationDates[i]
            val packageNum = if(packagesNumber.size <= i) mutableListOf() else packagesNumber[i]
            val price = if(prices.size <= i) mutableListOf() else prices[i]
            hospitals.add(
                Hospital(
                    i.toString(), false,
                    name, hospitalReference,
                    hospitalCoordinate[0], hospitalCoordinate[1],
                    addressHospital, phone,
                    expirationDate, packageNum, price
                )
            )
        }
        return hospitals
    }

    /**
     * Метод для получения координат аптек
     * @param hospitalReference список ссылок на аптеки
     */
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

    /**
     * Получение некоторой информации, которую невозможно получить из метода getTooltipInfo класса ITabletkaParser
     * @param table таблица аптек
     * @param contentClass тип информации
     * @param contentGetter функция, которая вызывается при обнаружении инфы аптеки
     */
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

    /**
     * Получение цен на препараты в аптеках
     * @param table таблица с аптеками
     */
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
    /**
     * Получение срока годности на препараты в аптеках
     * @param table таблица с аптеками
     */
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
    /**
     * Получение количества упаковок препаратов в аптеках
     * @param table таблица с аптеками
     */
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
    /**
     * Парсинг цены на аптеки
     * @param priceInfo список с информацией о цене в аптеке на препарат
     */
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