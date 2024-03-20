package com.diplom.tabletkaapp.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.R

abstract class AbstractAuthenticationFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    fun<Editor: EditText>  checkEditors(vararg editors: Editor): Boolean {
        for (editor in editors) {
            if (editor.text.toString().isEmpty()) {
                return true
            }
        }
        return false
    }
    fun <Editor : EditText> initTextWatchers(vararg editors: Editor) {
        for (editor in editors) {
            editor.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!editor.text.toString().isEmpty()) {
                        editor.setBackgroundResource(R.drawable.normal_background)
                        editor.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    fun <Editor : EditText> setErrorState(vararg editors: Editor) {
        for (editor in editors) {
            editor.setBackgroundResource(R.drawable.error_background)
            editor.error = "Wrong input"
        }
    }
}