package com.diplom.tabletkaapp.ui.search.filter

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import models.Hospital
import models.Medicine
import java.util.Locale

class ListFilterViewModel: ViewModel() {
    var title: String = ""
    var sortMask: Int = 0
    var minPrice: Double = 0.0
    var maxPrice: Double = 0.0
    //var userGeoPoint = GeoPoint(0.0, 0.0)

    fun filter(list: MutableList<AbstractModel>, choosedList: Boolean): MutableList<AbstractModel>{
        if(!choosedList){
            return filterMedicineList(list)
        }
        return filterPharmacyList(list)
    }
    private fun filterPharmacyList(list: MutableList<AbstractModel>): MutableList<AbstractModel>{
        return list.filter {
            val pharmacyElement = (it as Hospital)
            if(pharmacyElement.prices.size == 0) return@filter false
            return@filter pharmacyElement.prices[0] > minPrice &&
                    pharmacyElement.prices[0] < maxPrice
        } as MutableList<AbstractModel>
    }
    private fun filterMedicineList(list: MutableList<AbstractModel>): MutableList<AbstractModel>{
        return list.filter {
            val medicineElement = (it as Medicine)
            if(medicineElement.priceRange.size == 2) {
                return@filter ((medicineElement.priceRange[0] > minPrice) &&
                        (medicineElement.priceRange[1] < maxPrice))
            } else if(medicineElement.priceRange.size == 1){
                return@filter medicineElement.priceRange[0] > minPrice
            }
            return@filter false
        } as MutableList<AbstractModel>
    }

    fun sort(list: MutableList<AbstractModel>, choosedList: Boolean): MutableList<AbstractModel>{
        if(!choosedList){
            return sortMedicine(list)
        }
        return sortPharmacy(list)
    }
    private fun sortMedicine(list: MutableList<AbstractModel>): MutableList<AbstractModel>{
        val sortedList: MutableList<AbstractModel> = list
        if((sortMask and 1) == 1) {
            sortedList.sortBy {(it as Medicine).priceRange.firstOrNull()}
        }
        return sortedList
    }
    private fun sortPharmacy(list: MutableList<AbstractModel>): MutableList<AbstractModel>{
        val sortedList: MutableList<AbstractModel> = list
        if(sortMask and 1 == 1){
            sortedList.sortBy {(it as Hospital).prices.firstOrNull()}
        }
        if(sortMask and 2 == 2){
            sortedList.sortBy {
//                val pharmacy = it as Hospital
//                val dLat = Math.toRadians(userGeoPoint.latitude - pharmacy.latitude)
//                val dLon = Math.toRadians(userGeoPoint.longitude - pharmacy.longitude)
//                val lat1 = Math.toRadians(pharmacy.latitude)
//                val lat2 = Math.toRadians(userGeoPoint.latitude)
//                val a = sin(dLat / 2.0).pow(2.0) +
//                        cos(lat1) * cos(lat2) * sin(dLon / 2.0).pow(2.0)
//                return@sortBy 2 * atan2(sqrt(a), sqrt(1 - a))
                return@sortBy true
            }
        }
        return sortedList
    }
    fun filterByTitle(list: MutableList<AbstractModel>): MutableList<AbstractModel> {
        val regex = Regex(title.lowercase(Locale.ROOT))
        return list.filter {element -> regex.findAll(element.name.lowercase()).count() != 0 }
                as MutableList<AbstractModel>
    }
}