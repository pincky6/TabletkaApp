package models

import java.util.Date

data class Pharmacy(val name: String, val hospitalReference: String,
                    val latitude: Double, val longitude: Double,
                    val address: String, val phone: String,
                    val expirationDates: MutableList<Date>,
                    val packagesNumber: MutableList<Int>,
                    val prices: MutableList<Double>)
