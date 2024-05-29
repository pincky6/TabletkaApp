package com.diplom.tabletkaapp.views.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.view_models.map.MapViewModel
import models.Hospital
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapFragment: Fragment() {
    var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    val model = MapViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.overlays.add(RotationGestureOverlay(binding.mapView))

        initInfoBottom()
        initBackButton()
        return binding.root
    }

    private fun initInfoBottom(){
        model.geoPointsList = arguments?.getSerializable("pharmacyGeoPoints") as GeoPointsList
        if(model.geoPointsList.mutableList.size == 1){
            val hospital: Hospital? = arguments?.getSerializable("hospital") as Hospital?
            hospital?.let {
                (binding.mapBottomInfo as MapInfoBottomSheetFragment).setHospital(hospital)
            }
        } else {
            (binding.mapBottomInfo as MapInfoBottomSheetFragment).hideHospitalInfo()
        }
    }

    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}