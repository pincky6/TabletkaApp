package com.diplom.tabletkaapp.ui.search.filter

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import models.Medicine
import models.Pharmacy
import org.osmdroid.util.GeoPoint
import java.util.Locale

class ListSettingsViewModel: ViewModel() {
    var title: String = ""
    var sortMask: Int = 0
    var minPrice: Double = 0.0
    var maxPrice: Double = 0.0
    var userGeoPoint = GeoPoint(0.0, 0.0)
    fun sortMedicine(list: MutableList<AbstractFirebaseModel>){
        list.sortBy {
            var priceSort = false
            if((sortMask and 1) == 1){
                priceSort = ((it as Medicine).priceRange[0] > minPrice &&
                        (it as Medicine).priceRange[1] < maxPrice)
            }
            priceSort
        }
    }
    fun sortPharmacy(list: MutableList<AbstractFirebaseModel>){
        list.sortBy {
            var priceSort = false
            if((sortMask and 1) == 1){
                priceSort = ((it as Pharmacy).prices[0] > minPrice &&
                        (it as Pharmacy).prices[0] < maxPrice)
            }

            priceSort
        }
    }
    fun filterByTitle(medicines: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel> {
        val regex = Regex(title.lowercase(Locale.ROOT))
        return medicines.filter {medicine -> regex.findAll(medicine.name.lowercase()).count() != 0 }
                as MutableList<AbstractFirebaseModel>
    }
}