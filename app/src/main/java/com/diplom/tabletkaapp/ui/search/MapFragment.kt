package com.diplom.tabletkaapp.ui.search

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment: Fragment() {
    private var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    var locationOverlay: MyLocationNewOverlay? = null
    var locationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                getLocation()
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapVew.setTileSource(TileSourceFactory.MAPNIK)
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                getLocation()
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        setMarkers(GeoPoint(53.138429, 29.207931), "noth")
        binding.mapVew.setMultiTouchControls(true)
        binding.mapVew.overlays.add(RotationGestureOverlay(binding.mapVew))
//        setMarkers(startPoint, "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getLocation(){
        val gpsProvider = GpsMyLocationProvider(context)
        gpsProvider.locationSources.add(LocationManager.NETWORK_PROVIDER)
        val locationOverlay = MyLocationNewOverlay(gpsProvider, binding.mapVew)
        locationOverlay.enableMyLocation()
        binding.mapVew.overlays.add(locationOverlay)
        binding.mapVew.invalidate()
        locationManager  = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {
                val point: GeoPoint = GeoPoint(location.latitude, location.longitude)
                binding.mapVew.controller.setCenter(point)
                binding.mapVew.controller.setZoom(22.0)
            }
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0.0f, object:
                LocationListener {
                override fun onLocationChanged(location: Location){
                    val point: GeoPoint = GeoPoint(location.latitude, location.longitude)
                   // binding.mapVew.controller.setCenter(point)
                }


                override fun onStatusChanged(var1: String, var2: Int, var3: Bundle){

                }

                override fun onProviderEnabled(var1: String){

                }

                override fun onProviderDisabled(var1: String){

                }
            })
        }
    }
    private fun setMarkers(geoPoint: GeoPoint, name: String)
    {
        context?.let {
            val marker = Marker(binding.mapVew)
            marker.position = geoPoint
            marker.icon = ContextCompat.getDrawable(it, R.drawable.baseline_location_on_24)
            marker.title = "text mark"
            marker.setOnMarkerClickListener({marker, mapview->
                buildRoadMap(geoPoint)
            return@setOnMarkerClickListener true
            })
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            binding.mapVew.overlays.add(marker)
            binding.mapVew.invalidate()
        }
    }
    private fun buildRoadMap(endPoint: GeoPoint) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.mapVew.overlays.removeAll{
                it is Polyline
            }
            CoroutineScope(Dispatchers.IO).launch {
                val roadManager = OSRMRoadManager(context, System.getProperty("http.agent"))
                roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)
                val location: Location? = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                location?.let {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    val wayPoints = arrayListOf<GeoPoint>(geoPoint, GeoPoint(53.138429, 29.207931))
                    val road = roadManager.getRoad(wayPoints)
                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                    withContext(Dispatchers.Main) {
                        binding.mapVew.overlays.add(roadOverlay)
                        binding.mapVew.invalidate()
                    }
                }
            }
        }

    }
}