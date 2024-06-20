package com.diplom.tabletkaapp.firebase.authentication

import android.content.Context
import android.widget.Toast
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentRegisterBinding
import com.diplom.tabletkaapp.util.EditorsUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

/**
 * Класс для работы с регистрацией в Firebase Authentication
 */
object FirebaseSignUpRepository {
    /**
     * Объект для работы с Firebase Authentication
     */
    private var _auth: FirebaseAuth? = null
    /**
     * Вспомогательный геттер с проверкой на null
     */
    val auth get() = _auth!!

    init {
        /**
         * Инициализация объекта
         */
        _auth = FirebaseAuth.getInstance()
    }

    /**
     * Метод для создания аккаунта связанный с почтой и паролем
     * @param gmail почта
     * @param password пароль
     * @param onCompleteListener слушатель завершения работы
     * @param onFailrueSignListener слушатель неудачного завершения работы
     */
    fun createUserWithGmailAndPassword(
        gmail: String,
        password: String,
        onCompleteListener: OnCompleteSignListener?,
        onFailrueSignListener: OnFailrueSignListener?
    ) {
        auth.createUserWithEmailAndPassword(gmail, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                onCompleteListener?.completeTask(task.isSuccessful)
            }.addOnFailureListener { e: Exception ->
                onFailrueSignListener?.failrueTask(e)
            }
    }
}