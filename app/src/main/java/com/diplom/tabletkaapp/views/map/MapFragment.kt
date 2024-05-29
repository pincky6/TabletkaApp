package com.diplom.tabletkaapp.views.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.view_models.map.MapViewModel
import models.Hospital
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapFragment: Fragment() {
    var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    var mapInfoBottomSheetFragment: MapInfoBottomSheetFragment? = null
    val model = MapViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSystemService(requireContext(), LocationManager::class.java)?.let {
            model.locationManager = it
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        model.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object:
            LocationListener {
            override fun onLocationChanged(location: Location){
                if(model.currentGeoPoint != GeoPoint(location.latitude, location.longitude)) {
                    model.currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                    //_binding?.mapView?.controller?.setCenter(model.currentGeoPoint)
                }
            }


            override fun onStatusChanged(var1: String, var2: Int, var3: Bundle){

            }

            override fun onProviderEnabled(var1: String){

            }

            override fun onProviderDisabled(var1: String){

            }
        })
        ;
        val location = model.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            model.currentGeoPoint = GeoPoint (location.getLatitude(), location.getLongitude());
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.overlays.add(RotationGestureOverlay(binding.mapView))
        mapInfoBottomSheetFragment =
            childFragmentManager.findFragmentById(R.id.map_bottom_info) as MapInfoBottomSheetFragment?;
        initInfoBottom()
        initBackButton()
        model.setMarkers(requireContext(), binding)
        return binding.root
    }

    private fun initInfoBottom(){
        model.geoPointsList = arguments?.getSerializable("pharmacyGeoPoints") as GeoPointsList
        val hospital: Hospital? = arguments?.getSerializable("hospital") as Hospital?
        if(model.geoPointsList.mutableList.size == 1 && hospital != null){
            mapInfoBottomSheetFragment?.setHospital(hospital)
        } else {
            mapInfoBottomSheetFragment?.hideHospitalInfo()
            model.currentGeoPoint?.let {
                model.setZoom(binding, it)
            }
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