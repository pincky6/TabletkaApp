package com.diplom.tabletkaapp.ui.search.holders

import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import models.Medicine

class MedicineHolder(
    val binding: ItemMedicineBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(medicine: Medicine){
        binding.name.text = medicine.name
        binding.name.setOnClickListener {

        }
        binding.compound.text = medicine.compound
        binding.companyName.text = medicine.companyName
        binding.companyName.setOnClickListener{

        }
        binding.recipe.text = medicine.recipe
        binding.recipeInfo.text = medicine.recipeInfo
        if(medicine.priceRange.size == 2){
            binding.price.text = "${medicine.priceRange[0]}-${medicine.priceRange[1]}"
        } else if(medicine.priceRange.size == 1) {
            binding.price.text = "${medicine.priceRange[0]}"
        } else {
            binding.price.text = "Нет в продаже"
        }
        binding.addWishListButton.setOnClickListener {
            if(!medicine.wish){
                binding.addWishListButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                binding.addWishListButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
            medicine.wish = !medicine.wish
        }
    }
}