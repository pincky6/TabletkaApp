package com.diplom.tabletkaapp.views.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBottomSheetBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.view_models.map.MapBottomSheetViewModel
import models.Hospital

class MapInfoBottomSheetFragment: Fragment() {
    var _binding: FragmentMapBottomSheetBinding? = null
    val binding get() = _binding!!
    val model = MapBottomSheetViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBottomSheetBinding.inflate(inflater, container, false)

        binding.hospitalInfoPanel.showGeolocationButton.visibility = View.GONE
        binding.hospitalInfoPanel.pricesRecyclerView.visibility = View.GONE
        binding.hospitalInfoPanel.showMedicineInfoButton.visibility = View.GONE
        binding.hospitalInfoPanel.hospitalWishButton
        initWishButton()
        binding.toogleButton.setOnClickListener {
            if (binding.root.height == binding.toogleButton.layoutParams.height) {
                expandPanel()
            } else {
                collapsePanel()
            }
        }
        setInfoContent()
        return binding.root
    }

    private fun initWishButton(){
        binding.hospitalInfoPanel.hospitalWishButton.setImageResource(
            if(model.hospital?.wish ?: false) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.hospitalInfoPanel.hospitalWishButton.setOnClickListener {
            if(!FirebaseSingInRepository.checkUserExistWithWarningDialog(binding.root.context)){
                return@setOnClickListener
            }
            model.hospital?.wish = !model.hospital?.wish!!
            model.hospital?.let {
                if(it.wish){
                    FirebaseHospitalDatabase.add(it)
                    binding.hospitalInfoPanel.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    FirebaseHospitalDatabase.delete(it)
                    binding.hospitalInfoPanel.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
    }

    fun setHospital(newHospital: Hospital){
        model.hospital = newHospital
        setInfoContent()
    }
    private fun setInfoContent(){
        if(_binding != null) {
            binding.hospitalInfoPanel.name.text = model.hospital?.name
            binding.hospitalInfoPanel.address.text =model.hospital?.address
            binding.hospitalInfoPanel.phone.text = model.hospital?.phone
        }

    }

    fun hideHospitalInfo(){
        if(_binding != null) {
            binding.hospitalInfoPanel.root.visibility = View.GONE
        }
    }

    private fun expandPanel() {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                binding.toogleButton.setImageResource(R.drawable.baseline_expand_less_24)
                binding.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                binding.root.requestLayout()
            }
        }
        animation.duration = 300
        binding.hospitalInfoPanel.root.visibility = View.VISIBLE
        binding.root.startAnimation(animation)
    }
    private fun collapsePanel() {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)

                binding.toogleButton.setImageResource(R.drawable.baseline_expand_more_24)
                binding.root.layoutParams.height = binding.toogleButton.layoutParams.height
                binding.root.requestLayout()
            }
        }
        animation.duration = 300
        binding.hospitalInfoPanel.root.visibility = View.GONE
        binding.root.startAnimation(animation)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}