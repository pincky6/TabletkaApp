package com.diplom.tabletkaapp.util

import models.Hospital
import models.Medicine
import kotlin.math.abs

object ComparatorUtil {
    fun compare(medicineLhs: Medicine, medicineRhs: Medicine): Boolean{
        return !(medicineLhs.name             != medicineRhs.name ||
                medicineLhs.compound          != medicineRhs.compound ||
                medicineLhs.recipe            != medicineRhs.recipe ||
                medicineLhs.recipeInfo        != medicineRhs.recipeInfo ||
                medicineLhs.companyName       != medicineRhs.companyName ||
                medicineLhs.country           != medicineRhs.country)
    }

    fun compare(hospitalLhs: Hospital, hospitalRhs: Hospital): Boolean{
        return !(hospitalLhs.name             != hospitalRhs.name ||
                hospitalLhs.address           != hospitalRhs.address ||
                hospitalLhs.phone             != hospitalRhs.phone ||
                abs(hospitalLhs.latitude - hospitalRhs.latitude) > 0.001 ||
                abs(hospitalLhs.longitude - hospitalRhs.longitude) > 0.001)
    }
}