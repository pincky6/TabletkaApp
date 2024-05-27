package com.diplom.tabletkaapp.views

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentInfoBinding

class InfoFragment: Fragment() {
    var binding_: FragmentInfoBinding? = null
    val binding get() = binding_!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentInfoBinding.inflate(inflater, container, false)
        binding.infoTextView.text = arguments?.getString("info")
        Linkify.addLinks(binding.infoTextView, Linkify.WEB_URLS)
        binding.infoTextView.movementMethod = LinkMovementMethod.getInstance()
        initBackButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}