package com.diplom.tabletkaapp.views.lists.holders

import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemHospitalShortBinding
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.views.lists.simple_lists.HospitalRegionFragmentDirections
import org.osmdroid.util.GeoPoint

class HospitalShortHolder(val binding: ItemHospitalShortBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(hospital: HospitalShort){
        binding.tvHospitalName.text = hospital.name
        binding.tvHospitalAddress.text = hospital.address
        binding.tvHospitalPhone.text = hospital.phone
        binding.tvHospitalOpenState.text = hospital.openState
        binding.showGeolocationButton.setOnClickListener {
            val geoPoint =  GeoPointsList(mutableListOf(GeoPoint(hospital.latitude, hospital.latitude)))
            val hospitals = HospitalsList(mutableListOf(hospital))
            findNavController(binding.root).navigate(
                HospitalRegionFragmentDirections.actionHospitalRegionFragmentToMapFragment(geoPoint, hospitals)
            )
        }
    }
}