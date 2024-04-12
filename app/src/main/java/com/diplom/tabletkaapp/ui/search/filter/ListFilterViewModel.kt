package com.diplom.tabletkaapp.ui.search.filter

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import models.Medicine
import models.Pharmacy
import org.osmdroid.util.GeoPoint
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class ListFilterViewModel: ViewModel() {
    var title: String = ""
    var sortMask: Int = 0
    var minPrice: Double = 0.0
    var maxPrice: Double = 0.0
    var userGeoPoint = GeoPoint(0.0, 0.0)

    fun filter(list: MutableList<AbstractFirebaseModel>, choosedList: Boolean): MutableList<AbstractFirebaseModel>{
        if(!choosedList){
            return filterMedicineList(list)
        }
        return filterPharmacyList(list)
    }
    private fun filterPharmacyList(list: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel>{
        return list.filter {
            val pharmacyElement = (it as Pharmacy)
            if(pharmacyElement.prices.size == 0) return@filter false
            return@filter pharmacyElement.prices[0] > minPrice &&
                    pharmacyElement.prices[0] < maxPrice
        } as MutableList<AbstractFirebaseModel>
    }
    private fun filterMedicineList(list: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel>{
        return list.filter {
            val medicineElement = (it as Medicine)
            if(medicineElement.priceRange.size == 2) {
                return@filter ((medicineElement.priceRange[0] > minPrice) &&
                        (medicineElement.priceRange[1] < maxPrice))
            } else if(medicineElement.priceRange.size == 1){
                return@filter medicineElement.priceRange[0] > minPrice
            }
            return@filter false
        } as MutableList<AbstractFirebaseModel>
    }

    fun sort(list: MutableList<AbstractFirebaseModel>, choosedList: Boolean): MutableList<AbstractFirebaseModel>{
        if(!choosedList){
            return sortMedicine(list)
        }
        return sortPharmacy(list)
    }
    private fun sortMedicine(list: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel>{
        val sortedList: MutableList<AbstractFirebaseModel> = list
        if((sortMask and 1) == 1) {
            sortedList.sortBy {(it as Medicine).priceRange.firstOrNull()}
        }
        return sortedList
    }
    private fun sortPharmacy(list: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel>{
        val sortedList: MutableList<AbstractFirebaseModel> = list
        if(sortMask and 1 == 1){
            sortedList.sortBy {(it as Pharmacy).prices.firstOrNull()}
        }
        if(sortMask and 2 == 2){
            sortedList.sortBy {
                val R = 6371
                val dLat = Math.toRadians(userGeoPoint.latitude - (it as Pharmacy).latitude)
                val dLon = Math.toRadians(userGeoPoint.longitude - (it as Pharmacy).longitude)
                val lat1 = Math.toRadians((it as Pharmacy).latitude)
                val lat2 = Math.toRadians(userGeoPoint.latitude)
                val a = sin(dLat / 2.0).pow(2.0) +
                        cos(lat1) * cos(lat2) * sin(dLon / 2.0).pow(2.0)
                return@sortBy 2 * atan2(sqrt(a), sqrt(1 - a))
            }
        }
        return sortedList
    }
    fun filterByTitle(list: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel> {
        val regex = Regex(title.lowercase(Locale.ROOT))
        return list.filter {element -> regex.findAll(element.name.lowercase()).count() != 0 }
                as MutableList<AbstractFirebaseModel>
    }
}