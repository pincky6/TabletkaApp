package com.diplom.tabletkaapp.models

import java.io.Serializable

abstract class AbstractModel(open var id: String, open var wish: Boolean,
                             open val name: String) {
}