package com.diplom.tabletkaapp.ui.search.filter

import models.Medicine

class Filter {
    var title: String = ""
    fun filterByTitle(medicines: MutableList<Medicine>): MutableList<Medicine> {
        return medicines.filter {medicine -> title in medicine.name } as MutableList<Medicine>
    }
}