package com.diplom.tabletkaapp.ui.search.listeners

import com.diplom.tabletkaapp.models.PointModel

fun interface OnNavigationButtonClicked {
    fun click(pointModel: PointModel)
}