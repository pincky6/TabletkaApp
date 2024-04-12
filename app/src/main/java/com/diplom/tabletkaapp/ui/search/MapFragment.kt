package com.diplom.tabletkaapp.ui.search

import android.Manifest
import android.app.PendingIntent
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
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import com.diplom.tabletkaapp.models.PointModel
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
    var locationManager: LocationManager? = null
    private var showBottomSheet: Boolean = false
    val model = MapViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapVew.setTileSource(TileSourceFactory.MAPNIK)
        initBottomSheetButtons()
        binding.mapVew.setMultiTouchControls(true)
        binding.mapVew.overlays.add(RotationGestureOverlay(binding.mapVew))
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                initLocation()
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        model.geoPoints = (arguments?.getSerializable("pharmacyGeoPoints") as GeoPointsListArgs).geoPointsList
        model.geoPoints?.let {
            for(point in it)
                setMarker(GeoPoint(point.geoPoint.latitude, point.geoPoint.longitude), point.name)
        }
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { v: View ->
            findNavController().popBackStack()
        }
//        setMarkers(startPoint, "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initLocation(){
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
                model.currentMapGeoPoint = GeoPoint(location.latitude, location.longitude)
                binding.mapVew.controller.setCenter(model.currentMapGeoPoint)
                binding.mapVew.controller.setZoom(12.0)
            }
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10.0f, object:
                LocationListener {
                override fun onLocationChanged(location: Location){
                    if(model.currentMapGeoPoint != GeoPoint(location.latitude, location.longitude)) {
                        model.currentMapGeoPoint = GeoPoint(location.latitude, location.longitude)
                        binding.mapVew.controller.setCenter(model.currentMapGeoPoint)
                    }
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
    private fun setMarker(geoPoint: GeoPoint, name: String)
    {
        context?.let {
            val marker = Marker(binding.mapVew)
            marker.position = geoPoint
            marker.icon = ContextCompat.getDrawable(it, R.drawable.baseline_location_on_24)
            marker.title = name
            marker.setOnMarkerClickListener { marker, _ ->
                if (!showBottomSheet) {
                    model.currentGeoPoint = geoPoint
                    buildRoadMap(geoPoint, OSRMRoadManager.MEAN_BY_CAR)
                    binding.include.buildRoadMapLayout.visibility = View.VISIBLE
                    showBottomSheet = true
                } else if(model.currentMarker?.position != marker.position){
                    binding.mapVew.overlays.removeAll {
                        it is Polyline
                    }
                    model.currentGeoPoint = geoPoint
                    model.currentMarker = marker
                    buildRoadMap(model.currentGeoPoint, OSRMRoadManager.MEAN_BY_CAR)
                    binding.include.buildRoadMapLayout.visibility = View.VISIBLE
                    showBottomSheet = true
                }
                else{
                    binding.mapVew.overlays.removeAll {
                        it is Polyline
                    }
                    binding.include.buildRoadMapLayout.visibility = View.GONE
                    showBottomSheet = false
                }
                return@setOnMarkerClickListener true
            }
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            binding.mapVew.overlays.add(marker)
            binding.mapVew.invalidate()
        }
    }
    private fun buildRoadMap(endPoint: GeoPoint, roadManagerRoadChoose: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val roadManager = OSRMRoadManager(context, System.getProperty("http.agent"))
                roadManager.setMean(roadManagerRoadChoose)
                val location: Location? = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                location?.let {
                    model.currentGeoPoint = endPoint
                    val userLocation = GeoPoint(it.latitude, it.longitude)
                    val wayPoints = arrayListOf(userLocation, model.currentGeoPoint)
                    val road = roadManager.getRoad(wayPoints)
                    withContext(Dispatchers.Main) {
                        binding.mapVew.overlays.removeAll{
                            it is Polyline
                        }
                        val roadOverlay = RoadManager.buildRoadOverlay(road)
                        binding.mapVew.overlays.add(roadOverlay)
                        binding.mapVew.invalidate()
                    }
                }
            }
        }
    }
    private fun initBottomSheetButtons(){
        binding.include.buildOnFootRoadButton.setOnClickListener {
            buildRoadMap(model.currentGeoPoint, OSRMRoadManager.MEAN_BY_FOOT)
        }
        binding.include.buildBikeRoadButton.setOnClickListener {
            buildRoadMap(model.currentGeoPoint, OSRMRoadManager.MEAN_BY_BIKE)
        }
        binding.include.buildOnCarButton.setOnClickListener {
            buildRoadMap(model.currentGeoPoint, OSRMRoadManager.MEAN_BY_CAR)
        }
    }
    fun onLocationChanged(location: Location) {
        val point: GeoPoint = GeoPoint(location.latitude, location.longitude)
        binding.mapVew.controller.setCenter(point)
    }
}