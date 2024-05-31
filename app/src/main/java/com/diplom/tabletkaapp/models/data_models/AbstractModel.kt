package com.diplom.tabletkaapp.models

import java.io.Serializable

abstract class AbstractModel(open val id: String, open var wish: Boolean,
                             open val name: String) {
}