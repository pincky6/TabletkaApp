package com.diplom.tabletkaapp.ui.search.holders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemPharmacyBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.firebase.database.FirebasePharmacyDatabase
import com.diplom.tabletkaapp.models.PointModel
import com.diplom.tabletkaapp.ui.search.adapters.MedicineInfoAdapter
import com.diplom.tabletkaapp.ui.search.listeners.OnNavigationButtonClicked
import models.Pharmacy
import org.osmdroid.util.GeoPoint

class PharmacyHolder(
    var binding: ItemPharmacyBinding,
    var show: Boolean
): RecyclerView.ViewHolder(binding.root) {
    fun bind(pharmacy: Pharmacy,
             onNavigationButtonClicked: OnNavigationButtonClicked?,
             onWishListButtonClicked: (()->Unit)?){
        show = false
        binding.name.text = pharmacy.name
        binding.address.text = pharmacy.address
        binding.phone.text = pharmacy.phone
        binding.pricesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.pricesRecyclerView.adapter = MedicineInfoAdapter(pharmacy)
        binding.pricesRecyclerView.visibility = View.GONE
        binding.showGeolocationButton.setOnClickListener {
            onNavigationButtonClicked?.click(PointModel(pharmacy.name,
                GeoPoint(pharmacy.latitude, pharmacy.longitude)))
        }
        binding.addWishListButton.setImageResource(
            if(pharmacy.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.addWishListButton.setOnClickListener {
            pharmacy.wish = !pharmacy.wish
            if(pharmacy.wish){
                FirebasePharmacyDatabase.add(pharmacy)
                binding.addWishListButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                FirebasePharmacyDatabase.delete(pharmacy)
                binding.addWishListButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
            if (onWishListButtonClicked != null) {
                onWishListButtonClicked()
            }
        }
        initShowMedicineButton(pharmacy)
    }
    private fun initShowMedicineButton(pharmacy: Pharmacy) {
        binding.showMedicineInfoButton.setOnClickListener { v ->
            if (!show) {
                setPharmacyPriceVisibility(R.drawable.baseline_expand_more_24, pharmacy,
                        View.VISIBLE, true)
            } else {
                setPharmacyPriceVisibility(R.drawable.baseline_expand_less_24, null,
                    View.INVISIBLE, false)
            }
        }
    }
    private fun setPharmacyPriceVisibility(imageResource: Int, pharmacy: Pharmacy?,
                                           visibleFlag: Int, flag: Boolean){
        binding.showMedicineInfoButton.setImageResource(imageResource)
        (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).setPharmacy(pharmacy)
        (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).notifyDataSetChanged()
        binding.pricesRecyclerView.visibility = visibleFlag
        show = flag
    }
}