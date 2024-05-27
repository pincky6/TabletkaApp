package com.diplom.tabletkaapp.ui.search.holders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.views.lists.simple_lists.MedicineModelListDirections
import models.Medicine

class MedicineHolder(
    val binding: ItemMedicineBinding
): RecyclerView.ViewHolder(binding.root) {
//    fun bind(medicine: Medicine,
//             onMedicineClicked: ((()->Unit)?),
//             onWishListButtonClicked: (()->Unit)?){
    fun bind(medicine: Medicine){
        binding.name.text = medicine.name
        binding.compound.text = medicine.compound
        binding.companyName.text = medicine.companyName
        binding.recipe.text = medicine.recipe
        binding.recipeInfo.text = medicine.recipeInfo
        if(medicine.priceRange.size == 2){
            binding.price.text = "${medicine.priceRange[0]}-${medicine.priceRange[1]}"
        } else if(medicine.priceRange.size == 1) {
            binding.price.text = "${medicine.priceRange[0]}"
        } else {
            binding.price.text = "Нет в продаже"
        }
        binding.wishButton.setImageResource(
            if(medicine.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.wishButton.setOnClickListener {
            medicine.wish = !medicine.wish
            if (!medicine.wish) {
//                   FirebaseMedicineDatabase.add(medicine)
                binding.wishButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
//                   FirebaseMedicineDatabase.delete(medicine)
                binding.wishButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
        binding.infoButton.setOnClickListener{
            findNavController(binding.root).navigate(
                MedicineModelListDirections.showInfoFragment(medicine.toString())
            )
        }
        binding.copyButton.setOnClickListener{
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Медикамент", medicine.toString())
            clipboard.setPrimaryClip(clip)
        }
    }
}