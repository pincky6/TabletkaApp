package com.diplom.tabletkaapp.ui.search.holders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemPharmacyBinding
import com.diplom.tabletkaapp.ui.search.adapters.MedicineInfoAdapter
import models.Pharmacy

class PharmacyHolder(
    var binding: ItemPharmacyBinding,
    var show: Boolean
): RecyclerView.ViewHolder(binding.root) {
    fun bind(pharmacy: Pharmacy){
        show = false
        binding.name.text = pharmacy.name
        binding.address.text = pharmacy.address
        binding.phone.text = pharmacy.phone
        binding.pricesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.pricesRecyclerView.adapter = MedicineInfoAdapter(pharmacy)
        binding.pricesRecyclerView.visibility = View.GONE
        initShowMedicineButton(pharmacy)
    }
    private fun initShowMedicineButton(pharmacy: Pharmacy) {
        binding.showMedicineInfoButton.setOnClickListener { v ->
            if (!show) {
                binding.showMedicineInfoButton.setImageResource(R.drawable.baseline_expand_more_24)
                (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).setPharmacy(pharmacy)
                (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).notifyDataSetChanged()
                binding.pricesRecyclerView.visibility = View.VISIBLE
                show = true
            } else {
                binding.showMedicineInfoButton.setImageResource(R.drawable.baseline_expand_less_24)
                (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).setPharmacy(null)
                (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).notifyDataSetChanged()
                binding.pricesRecyclerView.visibility = View.INVISIBLE
                show = false
            }
        }
    }
}