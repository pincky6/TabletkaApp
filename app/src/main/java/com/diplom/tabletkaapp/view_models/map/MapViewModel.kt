package com.diplom.tabletkaapp.view_models.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.views.map.MapFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Hospital
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

class MapViewModel: ViewModel() {
    lateinit var geoPointsList: GeoPointsList
    lateinit var locationManager: LocationManager
    var currentGeoPoint: GeoPoint? = null
    var currentHospital: AbstractModel? = null
    var currentMarker: Marker? = null
    var roadType = OSRMRoadManager.MEAN_BY_FOOT
    var hospitals: HospitalsList? = null
    fun setMarkers(context: Context, fragment: Fragment, binding: FragmentMapBinding){
        if(geoPointsList.mutableList.size == 0)
            return
        if(geoPointsList.mutableList.size == 1){
            setZoom(binding, geoPointsList.mutableList[0])
            hospitals?.list?.let {
                currentMarker = setMarker(context, binding, fragment,
                    it[0], geoPointsList.mutableList[0])

            }
            return
        }
        hospitals?.list?.let {
            for (i in 0 until geoPointsList.mutableList.size) {
                setMarker(
                    context, binding, fragment,
                    it[i], geoPointsList.mutableList[i]
                )
            }
        }
    }
    fun setZoom(binding: FragmentMapBinding, point: GeoPoint){
        binding.mapView.controller.setCenter(point)
        binding.mapView.controller.setZoom(12.0)
    }
    fun setMarker(context: Context, binding: FragmentMapBinding, fragment: Fragment,
                  abstractModel: AbstractModel, geoPoint: GeoPoint): Marker{
            val marker = Marker(binding.mapView)
            marker.position = geoPoint
            marker.icon = ContextCompat.getDrawable(context, R.drawable.baseline_location_on_24)
            marker.setOnMarkerClickListener { marker, _ ->
                if(currentMarker == marker){
                    currentMarker = null
                    fragment.childFragmentManager.setFragmentResult(MapFragment.HIDE_VIEW, Bundle())
                    return@setOnMarkerClickListener true
                }
                val bundle = Bundle()
                currentHospital = abstractModel
                if(currentHospital is Hospital) {
                    bundle.putSerializable(
                        MapFragment.MARKER_PRESSED_HOSPITAL,
                        abstractModel as Hospital
                    )
                    fragment.childFragmentManager.setFragmentResult(MapFragment.MARKER_PRESSED_HOSPITAL, bundle)
                } else {
                    bundle.putSerializable(
                        MapFragment.MARKER_PRESSERD_HOSPITAL_SHORT,
                        abstractModel as HospitalShort
                    )
                    fragment.childFragmentManager.setFragmentResult(MapFragment.MARKER_PRESSERD_HOSPITAL_SHORT, bundle)
                }
                currentMarker = marker
                binding.mapView.overlays.removeAll {
                    it is Polyline
                }
                return@setOnMarkerClickListener true
            }
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            binding.mapView.overlays.add(marker)
            binding.mapView.invalidate()
            return marker
        }
    fun getCurrentAddress(context: Context, binding: FragmentMapBinding): String{
        val geocoder = Geocoder(context, Locale.getDefault())
        var address: String = ""
        currentGeoPoint?.let {
            address =
                geocoder.getFromLocation(it.latitude, it.longitude, 1)?.get(0)?.getAddressLine(0).toString()
        }
        return address
    }

        fun buildRoadMap(context: Context, binding: FragmentMapBinding,
                         endPoint: GeoPoint, roadManagerRoadChoose: String) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    val roadManager = OSRMRoadManager(context, System.getProperty("http.agent"))
                    roadManager.setMean(roadManagerRoadChoose)
                    val location: Location? = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER)
                    location?.let {
                        val userLocation = GeoPoint(it.latitude, it.longitude)
                        val wayPoints = arrayListOf(userLocation, endPoint)
                        val road = roadManager.getRoad(wayPoints)
                        withContext(Dispatchers.Main) {
                            binding.mapView.overlays.removeAll{
                                it is Polyline
                            }
                            val roadOverlay = RoadManager.buildRoadOverlay(road)
                            binding.mapView.overlays.add(roadOverlay)
                            binding.mapView.invalidate()
                        }
                    }
                }
            }
        }

    fun removeRoad(binding: FragmentMapBinding, context: Context){
        binding.mapView.overlays.removeAll{
            it is Polyline
        }
    }
}

