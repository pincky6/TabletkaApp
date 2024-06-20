package com.diplom.tabletkaapp.view_models.map

import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.models.AbstractModel
import models.Hospital

/**
 * Класс модели-представления информационной менюшки карты
 * @param hospital аптека
 */
class MapBottomSheetViewModel: ViewModel() {
    var hospital: AbstractModel? = null
}