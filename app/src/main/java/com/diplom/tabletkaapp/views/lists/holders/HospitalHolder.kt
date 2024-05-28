package com.diplom.tabletkaapp.ui.search.holders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemHospitalBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.ui.search.adapters.MedicineInfoAdapter
import models.Hospital
import models.Medicine

class HospitalHolder(
    var binding: ItemHospitalBinding,
    var show: Boolean
): RecyclerView.ViewHolder(binding.root) {
    fun bind(hospital: Hospital,
             regionId: Int, medicineId: Long,
             requestId: Long, query: String,
             onWishListClicked: (()->Unit)?){
        show = false
        binding.name.text = hospital.name
        binding.address.text = hospital.address
        binding.phone.text = hospital.phone
        binding.pricesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.pricesRecyclerView.adapter = MedicineInfoAdapter(hospital)
        binding.pricesRecyclerView.visibility = View.GONE

        initWishButton(hospital, requestId, regionId, query, onWishListClicked)
        initShowMedicineButton(hospital)
    }
    private fun initShowMedicineButton(hospital: Hospital) {
        binding.showMedicineInfoButton.setOnClickListener { v ->
            if (!show) {
                setPharmacyPriceVisibility(R.drawable.baseline_expand_more_24, hospital,
                        View.VISIBLE, true)
            } else {
                setPharmacyPriceVisibility(R.drawable.baseline_expand_less_24, null,
                    View.INVISIBLE, false)
            }
        }
    }
    private fun initWishButton(hospital: Hospital, requestId: Long,
                               regionId: Int, query: String,
                               onWishListClicked: (()->Unit)?){
        binding.hospitalWishButton.setImageResource(
            if(hospital.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.hospitalWishButton.setOnClickListener {
            if(!FirebaseSingInRepository.checkUserExistWithWarningDialog(binding.root.context)){
                return@setOnClickListener
            }
            hospital.wish = !hospital.wish
            if(hospital.wish){

                FirebaseHospitalDatabase.add(hospital, requestId, regionId, query)
                binding.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                FirebaseHospitalDatabase.delete(hospital, requestId, regionId, query)
                binding.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
            onWishListClicked?.invoke()
        }
    }
    private fun setPharmacyPriceVisibility(imageResource: Int, hospital: Hospital?,
                                           visibleFlag: Int, flag: Boolean){
        binding.showMedicineInfoButton.setImageResource(imageResource)
        (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).setPharmacy(hospital)
        (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).notifyDataSetChanged()
        binding.pricesRecyclerView.visibility = visibleFlag
        show = flag
    }
}