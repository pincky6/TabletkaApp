package com.diplom.tabletkaapp.firebase.authentication

import android.widget.Toast
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.databinding.FragmentResetPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

/**
 * Класс для переустановки пароля в Firebase
 */
object FirebaseResetPasswordRepository {
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
     * Метод для переустановки пароля по gmail
     * @param gmail почта gmail
     * @param onCompleteListener слушатель завершения работы
     * @param onFailrueSignListener слушатель неудачного завершения работы
     */
    fun resetPasswordFromGmail(
        gmail: String,
        onCompleteListener: OnCompleteSignListener?,
        onFailrueSignListener: OnFailrueSignListener?
    ) {
        auth.sendPasswordResetEmail(gmail)
            .addOnCompleteListener { task: Task<Void?> ->
                onCompleteListener?.completeTask(task.isSuccessful)
            }
            .addOnFailureListener { exception ->
                onFailrueSignListener?.failrueTask(exception)
            }
    }
}