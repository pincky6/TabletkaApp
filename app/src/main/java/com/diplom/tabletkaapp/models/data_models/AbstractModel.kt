package com.diplom.tabletkaapp.models

import java.io.Serializable

/**
 * Абстрактный класс, который описывает нечто, что имеет идентификатор, флаг списка желания и название
 */
abstract class AbstractModel(open var id: String, open var wish: Boolean,
                             open val name: String) {
}