package com.diplom.tabletkaapp.models.data_models

import com.diplom.tabletkaapp.models.AbstractModel
import java.io.Serializable

/**
 * Класс описывающий список аптек
 */
data class HospitalsList(var list: MutableList<AbstractModel>): Serializable