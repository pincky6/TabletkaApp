package com.diplom.tabletkaapp.ui.search.filter

import com.diplom.tabletkaapp.models.AbstractFirebaseModel
import models.Medicine
import java.util.Locale

class Filter {
    var title: String = ""
    fun filterByTitle(medicines: MutableList<AbstractFirebaseModel>): MutableList<AbstractFirebaseModel> {
        val regex = Regex(title.lowercase(Locale.ROOT))
        return medicines.filter {medicine -> regex.findAll(medicine.name.lowercase()).count() != 0 }
                as MutableList<AbstractFirebaseModel>
    }
}