package com.diplom.tabletkaapp.parser

import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.util.UrlStrings
import models.Hospital
import models.Medicine
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.UUID

object MedicineParser: ITabletkaHealthParser()  {
    /**
     * Метод для получения медикаментов с определенной страницы сайта
     * @param name строка с которой будет создаваться запрос на сайт
     * @param regionId индентификатор региона
     * @param page страница таблетки
     */
    override fun parsePageFromName(name: String, regionId: Int, page: Int): MutableList<AbstractModel>{
        val pagedUrl = "${name}${UrlStrings.PAGE_CONDITION}${page}"
        return parseFromName(pagedUrl, regionId)
    }

    /**
     * Метод для получения списка медикаментов
     * @param name строка с которой будет создаваться запрос на сайт
     * @param regionId индентификатор региона
     */
    override fun parseFromName(name: String, regionId: Int): MutableList<AbstractModel>{
        val doc = Jsoup.connect("${UrlStrings.REQUEST_URL}${name}" +
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

        return getMedicines(names, medicinesReference,
                            compounds, compoundReference,
                            recipes, recipesInfo,
                            companies, companiesReference,
                            countries, prices, hospitalsCount)
    }

    /**
     * Получение цен на лекарство
     * @param table таблица с медикаментами
     * @param priceClass класс цены
     */
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

    /**
     * Парсинг цены на медикамент
     * @param price строка для парсинга цены
     */
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

    /**
     * Получение количества аптек у которых есть данный препарат
     */
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

    /**
     * Парсинг количества аптек с данных медикаментом
     * @param string строка для парсинга
     */
    private fun parseHospitalCount(string: String): MutableList<Int>{
        val hospitalInfo = string.split(" ")
        var hospitalCount: Int = 0
        if(hospitalInfo.size == 3){
            hospitalCount = hospitalInfo[1].toInt()
        }
        return listOf(hospitalCount).toMutableList()
    }

    /**
     * Метод для загрузки информации об медикаменте в список медикаментов
     * Если некоторые поля в таблице медикаментов отсутствовали, то вставляем пустую строку
     */
    private fun getMedicines(names: MutableList<String>, medicinesReferences: MutableList<String>,
                            compounds: MutableList<String>, compoundReferences: MutableList<String>,
                            recipes: MutableList<String>, recipesInfo: MutableList<String>,
                            companies: MutableList<String>, companiesReferences: MutableList<String>,
                            countries: MutableList<String>, prices: MutableList<MutableList<Double>>,
                            hospitalsCount: MutableList<Int>): MutableList<AbstractModel>
    {
        val medicines = mutableListOf<AbstractModel>()
        val size = mutableListOf(names.size, medicinesReferences.size,
                                 compounds.size, compoundReferences.size,
                                 recipes.size, recipesInfo.size,
                                 companies.size, companiesReferences.size,
                                 countries.size, prices.size,
                                 hospitalsCount.size).maxOrNull() ?: 0
        for(i in 0 until size)
        {
            val name = if(names.size <= i) "" else names[i]
            val medicineReference = if(medicinesReferences.size <= i) "" else medicinesReferences[i]
            val compound = if(compounds.size <= i) "" else compounds[i]
            val compoundReference = if(compoundReferences.size <= i) "" else compoundReferences[i]
            val recipe = if(recipes.size <= i) "" else recipes[i]
            val recipeInfo = if(recipesInfo.size <= i) "" else recipesInfo[i]
            val company = if(companies.size <= i) "" else companies[i]
            val companyReference = if(companiesReferences.size <= i) "" else companiesReferences[i]
            val country = if(countries.size <= i) "" else countries[i]
            val price = if(prices.size <= i) mutableListOf() else prices[i]
            val hospitalCount = if(hospitalsCount.size <= i) 0 else hospitalsCount[i]
            medicines.add(Medicine(i.toString(), false,
                                   name, medicineReference,
                                   compound, compoundReference,
                                   recipe, recipeInfo, company, companyReference,
                                   country, price, hospitalCount))
        }
        return medicines
    }
}