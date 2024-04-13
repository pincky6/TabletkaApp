package com.diplom.tabletkaapp.ui.search.holders

import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.ui.search.listeners.OnMedicineClickListener
import com.diplom.tabletkaapp.ui.search.listeners.OnNavigationButtonClicked
import models.Medicine

class MedicineHolder(
    val binding: ItemMedicineBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(medicine: Medicine,
             onCompanyNameClicked: OnMedicineClickListener?,
             onMedicineNameClicked: OnMedicineClickListener?,
             onCompoundClicked: OnMedicineClickListener?,
             onWishListButtonClicked: (()->Unit)?){
        binding.name.text = medicine.name
        binding.name.setOnClickListener {
            onMedicineNameClicked?.click(medicine.medicineReference)
        }
        binding.compound.text = medicine.compound
        binding.compound.setOnClickListener {
            onCompoundClicked?.click(medicine.compoundReference)
        }
        binding.companyName.text = medicine.companyName
        binding.companyName.setOnClickListener{
            onCompanyNameClicked?.click(medicine.companyName)
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
        binding.addWishListButton.setImageResource(
            if(medicine.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.addWishListButton.setOnClickListener {
            medicine.wish = !medicine.wish
            if(!medicine.wish){
                FirebaseMedicineDatabase.add(medicine)
                binding.addWishListButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                FirebaseMedicineDatabase.delete(medicine)
                binding.addWishListButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
            if (onWishListButtonClicked != null) {
                onWishListButtonClicked()
            }
        }
    }
}