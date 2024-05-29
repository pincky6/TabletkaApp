package com.diplom.tabletkaapp.view_models.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapViewModel: ViewModel() {
    lateinit var geoPointsList: GeoPointsList
    lateinit var locationManager: LocationManager
    var currentGeoPoint: GeoPoint? = null
    var currentMarker: Marker? = null
    var roadType = OSRMRoadManager.MEAN_BY_FOOT
    fun setMarker(context: Context, binding: FragmentMapBinding, geoPoint: GeoPoint){
                val marker = Marker(binding.mapView)
                marker.position = geoPoint
                marker.icon = ContextCompat.getDrawable(context, R.drawable.baseline_location_on_24)
//                marker.title = name
                marker.setOnMarkerClickListener { marker, _ ->
                    currentGeoPoint = geoPoint
                    currentMarker = marker
                    binding.mapView.overlays.removeAll {
                        it is Polyline
                    }
                    buildRoadMap(context, binding,
                        geoPoint, OSRMRoadManager.MEAN_BY_FOOT)
                    return@setOnMarkerClickListener true
                }
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                binding.mapView.overlays.add(marker)
                binding.mapView.invalidate()
        }

        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
                        currentGeoPoint = endPoint
                        val userLocation = GeoPoint(it.latitude, it.longitude)
                        val wayPoints = arrayListOf(userLocation, currentGeoPoint)
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
    }

