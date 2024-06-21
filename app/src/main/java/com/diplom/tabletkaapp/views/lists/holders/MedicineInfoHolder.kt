package com.diplom.tabletkaapp.ui.search.holders

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemMedicineInfoBinding
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Класс описывающий информацию о медикаментах, которые продаются в аптеке
 * @param binding привязка к элементам макета
 */
class MedicineInfoHolder(
    val binding: ItemMedicineInfoBinding
):   RecyclerView.ViewHolder(binding.root) {
    /**
     * Метод привзяки данных модели к ее представлению
     * Установка текста
     * @param expirationDate срок годности
     * @param packageNumber количество упаковок
     * @param price цена
     */
    fun bind(expirationDate: Date, packageNumber: Int, price: Double){
        val dateStr: String = if(SimpleDateFormat("MM/yyyy").format(expirationDate) == "01/1970"){
            binding.root.context.getString(R.string.check_with_the_pharmacy)
        } else {
            binding.root.context.getString(R.string.good_for) + ": "+ SimpleDateFormat("MM/yyyy").format(expirationDate)
        }
        binding.expirationDate.text = dateStr
        binding.packagesNumber.text = if(packageNumber == 0) {
            binding.root.context.getString(R.string.quantity_specify)
        } else {
            "${binding.root.context.getString(R.string.items_count)} ${packageNumber}"
        }
        binding.price.text = "${binding.root.context.getString(R.string.price)} ${price} p."
    }
}