package com.diplom.tabletkaapp.ui.search.holders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Query
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemMedicineBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.views.lists.simple_lists.MedicineModelListDirections
import models.Medicine

class MedicineHolder(
    val binding: ItemMedicineBinding,
    val isWish: Boolean
): RecyclerView.ViewHolder(binding.root) {
    fun bind(medicine: Medicine, query: String,
             regionId: Int, requestId: Long,
             onWishListClicked: (()->Unit)?){
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

        initWishButton(medicine, requestId, regionId, query, onWishListClicked)
        initCopyButton(medicine)
        initInfoButton(medicine)
        binding.root.setOnClickListener{
            findNavController(binding.root).navigate(
                MedicineModelListDirections.showHospitalModelList(query, requestId, regionId, medicine)
            )
        }
    }
    private fun initWishButton(medicine: Medicine, requestId: Long,
                               regionId: Int, query: String,
                               onWishListClicked: (()->Unit)?){
        binding.wishButton.setImageResource(
            if(medicine.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.wishButton.setOnClickListener {
            if(!FirebaseSingInRepository.checkUserExistWithWarningDialog(binding.root.context)){
                return@setOnClickListener
            }
            medicine.wish = !medicine.wish
            if(medicine.wish){
                FirebaseMedicineDatabase.add(medicine, requestId, regionId, query)
                binding.wishButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                FirebaseMedicineDatabase.delete(medicine, requestId, regionId, query)
                binding.wishButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
            onWishListClicked?.invoke()
        }
    }
    private fun initCopyButton(medicine: Medicine){
        binding.copyButton.setOnClickListener{
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Медикамент", medicine.toString())
            clipboard.setPrimaryClip(clip)
        }
    }
    private fun initInfoButton(medicine: Medicine){
        binding.infoButton.setOnClickListener{
            findNavController(binding.root).navigate(
                MedicineModelListDirections.showInfoFragment(medicine.toString())
            )
        }
    }
}