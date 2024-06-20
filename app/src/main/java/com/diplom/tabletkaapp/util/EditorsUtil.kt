package com.diplom.tabletkaapp.util

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.diplom.tabletkaapp.R

/**
 * Утилита для настройки полей ввода в приложении
 */
object EditorsUtil {
    /**
     * Проверка переданных полей ввода на пустоту
     */
    fun <Editor : EditText?> checkEditors(vararg editors: Editor): Boolean {
        for (editor in editors) {
            if (editor!!.getText().toString().isEmpty()) {
                return true
            }
        }
        return false
    }

    /**
     * Установка полей ввода в состояние ошибки
     */
    fun <Editor : EditText?> setErrorBackground(vararg editors: Editor) {
        for (editor in editors) {
            if (editor!!.getText().toString().isEmpty()) {
                editor.setBackgroundResource(R.drawable.error_background)
                editor.error = "Empty"
            }
        }
    }
    /**
     * Инициализация проверки на ввод. Если было изменение поля ввода, то меняем поле ввода на обычное состояние
     */
    fun <Editor : EditText?> initTextWatchers(vararg editors: Editor) {
        for (editor in editors) editor!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!editor.getText().toString().isEmpty()) {
                    editor.setBackgroundResource(R.drawable.normal_background)
                    editor.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    /**
     * Инициализация фильтров, которые не позволяют вводить пустые символы
     */
    fun <Editor : EditText> initTextFilters(vararg editors: Editor) {
        for (editor in editors) {
            editor.filters = arrayOf(InputFilter { source, start,end, dest, dstart, dend ->
                 var updatedSource = source
                if (source.isNotEmpty() && " " in source) {
                    updatedSource = source.toString().replace(" ", "")
                }
                updatedSource
            })
        }
    }


    fun <Editor : EditText?> setErrorState(vararg editors: Editor) {
        for (editor in editors) {
            editor!!.setBackgroundResource(R.drawable.error_background)
            editor.error = "Wrong input"
        }
    }
}
