package com.diplom.tabletkaapp.views.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentRegisterBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSignUpRepository
import com.diplom.tabletkaapp.firebase.authentication.OnFailrueSignListener
import com.diplom.tabletkaapp.util.EditorsUtil

/**
 * Класс описывающий окно регистрации
 */
class RegisterFragment: Fragment() {
    /**
     * Привязка к элементам макета
     */
    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding!!
    /**
    * Метод создания представления
    * Здесь производится инициалиазция кнопок, полей ввода и тулбара
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        initButtons()
        initEditTexts()
        initToolbar()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Инициализация кнопок.
     * При нажатии на кнопку регистрации проверяются данные
     * При неверном формате ввода ставится фон ошибки для полей ввода
     * Иначе создается аккаунт в Firebase
     */
    fun initButtons() {
        binding.signUpButton.setOnClickListener { v ->
            if (EditorsUtil.checkEditors(binding.gmailEditText, binding.passwordEditText)) {
                EditorsUtil.setErrorBackground(binding.gmailEditText, binding.passwordEditText)
                return@setOnClickListener
            }
            FirebaseSignUpRepository.createUserWithGmailAndPassword(    binding.gmailEditText.getText().toString().trim(),
                binding.passwordEditText.getText().toString().trim(),
                { successful ->
                    if (successful) {
                        Toast.makeText(context, getString(R.string.you_registered), Toast.LENGTH_SHORT).show()
                        binding.getRoot().findNavController().popBackStack()
                    } else {
                        EditorsUtil.setErrorState(binding.passwordEditText)
                        Toast.makeText(
                            context,
                            getString(R.string.check_gmail_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                object : OnFailrueSignListener {
                    override fun failrueTask(exception: Exception) {
                        Toast.makeText(
                            context,
                            exception.message,
                            Toast.LENGTH_LONG
                        ).show()
                        exception.printStackTrace()
                    }
                })
        }
    }
    /**
     * Инициализация наблюдателей и фильтров полей ввода
     */
    fun initEditTexts(){
        EditorsUtil.initTextWatchers(binding.gmailEditText, binding.passwordEditText)
        EditorsUtil.initTextFilters(binding.gmailEditText, binding.passwordEditText)
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
}