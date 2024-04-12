package com.diplom.tabletkaapp.models

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

abstract class AbstractFirebaseModel(open var id: String, open val name: String, open var wish: Boolean) {

}