package com.diplom.tabletkaapp.ui.search.holders

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.databinding.ItemMedicineInfoBinding
import java.text.SimpleDateFormat
import java.util.Date

class MedicineInfoHolder(
    val binding: ItemMedicineInfoBinding
):   RecyclerView.ViewHolder(binding.root) {
    fun bind(expirationDate: Date, packageNumber: Int, price: Double){
        val dateStr: String = SimpleDateFormat("MM/yyyy").format(expirationDate);
        binding.expirationDate.text = "Годен до ${dateStr}"
        binding.packagesNumber.text = if(packageNumber == 0) {
            "количество уточняйте"
        } else {
            "количество упаковок: ${packageNumber}"
        }
        binding.price.text = "${price} p."
    }
}