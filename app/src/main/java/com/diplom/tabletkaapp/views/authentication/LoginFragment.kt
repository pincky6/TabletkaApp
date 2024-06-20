package com.diplom.tabletkaapp.views.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentLoginBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.authentication.OnFailrueSignListener
import com.diplom.tabletkaapp.util.EditorsUtil
import com.google.firebase.auth.FirebaseAuth

/**
 * Класс представления окна логина
 */
class LoginFragment: Fragment() {
    /**
     * Привязка к элементам макета
     */
    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!

    /**
     * Метод обозначающий начало работы фрагмента
     * В нем инициализируется базовый класс, а также текстовые поля, также производится получение gmail и установка
     * в поле ввода gmail если тот есть
     */
    override fun onStart() {
        super.onStart()
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        if (FirebaseSingInRepository.isUserExist && FirebaseSingInRepository.isVerified) {
            FirebaseAuth.getInstance().currentUser?.let {
                it.email?.let {
                    binding.gmailEditText.setText(it)
                }
            }
        }
    }

    /**
     * Метод создания представления
     * Здесь производится инициалиазция кнопок, полей ввода и тулбара
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        initButtons()
        initEditTexts()
        initToolbar()
        return binding.root
    }

    /**
     * Метод уничтожения представления
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Инициализация кнопок входа, где проверяются данные и если те корректны производится вход в аккаунт, если уже
     * было пройдена верификация, иначе производится верификация и после этого вход в аккаунт
     * Если данные не валидны, то устанавливаем тему ошибки для поля ввода
     * Кнопка регистрации и кнопка переустановки пароля перемещают в соответствующие представления
     */
    private fun initButtons() {
        binding.signInButton.setOnClickListener { v ->
            if (EditorsUtil.checkEditors(binding.gmailEditText, binding.passwordEditText)) {
                EditorsUtil.setErrorBackground(binding.gmailEditText, binding.passwordEditText)
                return@setOnClickListener
            }
            FirebaseSingInRepository.signInWithGmailAndPassword(
                binding.gmailEditText.getText().toString().trim(),
                binding.passwordEditText.getText().toString().trim(),
                { successful: Boolean ->
                    if (!FirebaseSingInRepository.isVerified) {
                        Toast.makeText(
                            binding.root.context,
                            R.string.check_gmail,
                            Toast.LENGTH_LONG
                        ).show()
                        sendVerificationMessage()
                        return@signInWithGmailAndPassword
                    }
                    else
                    {
                        Toast.makeText(
                            binding.root.context,
                            getString(R.string.you_enter_in_account),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController(binding.root).popBackStack()
                    }
                    if (!successful) {
                        EditorsUtil.setErrorState(binding.passwordEditText)
                        Toast.makeText(
                            binding.root.context,
                            getString(R.string.check_gmail_error),
                            Toast.LENGTH_LONG
                        ).show()
                        return@signInWithGmailAndPassword
                    }
                },
                object : OnFailrueSignListener {
                    override fun failrueTask(exception: Exception) {
                        Toast.makeText(
                            binding.root.context,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        binding.signUpButton.setOnClickListener { v ->
            findNavController(binding.root).navigate(
                LoginFragmentDirections.showRegisterFragment()
            )
        }
        binding.resetPasswordButton.setOnClickListener { v ->
            findNavController(binding.root).navigate(
                LoginFragmentDirections.showResetPasswordFragment()
            )
        }
    }

    /**
     * Инициализация кнопки выхода из окна
     */
    private fun initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    /**
     * Инициализация наблюдателей и фильтров полей ввода
     */
    private fun initEditTexts(){
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        EditorsUtil.initTextFilters(binding.gmailEditText, binding.passwordEditText)
    }

    /**
     * Метод для отправки верификационного сообщения на почту
     */
    private fun sendVerificationMessage(){
        FirebaseSingInRepository.sendVerification({ successful: Boolean ->
            if (successful) {
                findNavController(binding.root).popBackStack()
                return@sendVerification
            }
        },
            object : OnFailrueSignListener {
                override fun failrueTask(exception: Exception) {
                    Toast.makeText(
                        binding.root.context,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}

