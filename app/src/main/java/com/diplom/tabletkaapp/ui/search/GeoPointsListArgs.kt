package com.diplom.tabletkaapp.ui.search

import com.diplom.tabletkaapp.models.PointModel
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class GeoPointsListArgs(
    val geoPointsList: MutableList<PointModel>
) : Serializable{
    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.defaultWriteObject()
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        `in`.defaultReadObject()
    }
}