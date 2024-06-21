package com.diplom.tabletkaapp.views

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentInfoBinding
import com.diplom.tabletkaapp.util.UrlStrings
import models.Medicine

class InfoFragment: Fragment() {
    var binding_: FragmentInfoBinding? = null
    val binding get() = binding_!!

    /**
     * Метод по установке текста для вывода информации о медикаменте
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentInfoBinding.inflate(inflater, container, false)
        val medicine = arguments?.getSerializable("medicine") as Medicine
        binding.medicineText.text = medicine.name
        linkText(binding.medicineReferenceText, UrlStrings.SITE_REFERENCE + medicine.medicineReference)
        binding.compoundText.text = medicine.compound
        linkText(binding.compoundReferenceText, UrlStrings.SITE_REFERENCE + medicine.compoundReference)
        binding.recipeText.text = medicine.recipe
        binding.recipeInfoText.text = medicine.recipeInfo
        binding.companyNameText.text = medicine.companyName
        linkText(binding.companyReferenceText, UrlStrings.SITE_REFERENCE + medicine.companyReference)
        binding.hospitalsCountText.text = medicine.hospitalCount.toString()
        initBackButton()
        return binding.root
    }

    /**
     * Привязка текста к ссылке на сайт для перехрда на сайт из приложения
     */
    private fun linkText(textView: TextView, reference: String){
        textView.setText(reference)
        Linkify.addLinks(textView, Linkify.WEB_URLS)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    /**
     * Инициализация кнопки выхода из окна
     */
    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}