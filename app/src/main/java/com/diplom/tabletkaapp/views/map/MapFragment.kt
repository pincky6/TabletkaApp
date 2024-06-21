package com.diplom.tabletkaapp.views.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.util.MapUtil
import com.diplom.tabletkaapp.view_models.map.MapViewModel
import models.Hospital
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Представление карт
 */
class MapFragment: Fragment() {
    var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    var mapInfoBottomSheetFragment: MapInfoBottomSheetFragment? = null
    val model = MapViewModel()

    /**
     * Метод создания обхекта.
     * Также в этом методе мы получаем информацию об геолокации пользователя
     */
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
        model.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, object:
            LocationListener {
            override fun onLocationChanged(location: Location){
                if(model.currentGeoPoint != GeoPoint(location.latitude, location.longitude)) {
                    model.currentGeoPoint = GeoPoint(location.latitude, location.longitude)
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
        val location = model.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            model.currentGeoPoint = GeoPoint (location.getLatitude(), location.getLongitude());
        }
    }

    /**
     * Метод по созданию представления
     * Инициализируются панели информации, навигации
     * Инициализация карты и способа взаимодействия с ней(мультитач, повороты)
     */
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
        model.geoPointsList = arguments?.getSerializable("pharmacyGeoPoints") as GeoPointsList
        model.hospitals = arguments?.getSerializable("hospitalList") as HospitalsList?
        if(model.hospitals == null || model.hospitals!!.list.size > 1){
            model.currentGeoPoint?.let { model.setZoom(binding, it) }
        }
        initInfoBottomListeners()
        initBackButton()
        initUserLocation()
        initMapNavigationBackButton()
        model.setMarkers(requireContext(), this, binding)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRoadListeners()

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * Инициализация нижнего меню и его слушателей
     * При запросе аптеки либо ничего не возвращаем либо аптеку
     */
    private fun initInfoBottomListeners(){
        childFragmentManager.setFragmentResultListener(
            MapInfoBottomSheetFragment.REQUEST_HOSPITAL,
            viewLifecycleOwner
        ){ _, _ ->
            if(model.hospitals == null || model.hospitals!!.list.size > 1){
                childFragmentManager.setFragmentResult(HOSPITAL_NULL_SEND, Bundle())
            } else{
                val bundle = Bundle()
                if(model.hospitals!!.list[0] is Hospital) {
                    bundle.putSerializable(MARKER_PRESSED_HOSPITAL, model.hospitals!!.list[0] as Hospital)
                    childFragmentManager.setFragmentResult(MARKER_PRESSED_HOSPITAL, bundle)
                } else {
                    bundle.putSerializable(MARKER_PRESSERD_HOSPITAL_SHORT, model.hospitals!!.list[0] as HospitalShort)
                    childFragmentManager.setFragmentResult(MARKER_PRESSERD_HOSPITAL_SHORT, bundle)
                }
            }
        }
    }

    /**
     * Получаем геолокацию пользователя
     */
    private fun initUserLocation(){
        val gpsProvider = GpsMyLocationProvider(context)
        gpsProvider.locationSources.add(LocationManager.NETWORK_PROVIDER)
        val locationOverlay = MyLocationNewOverlay(gpsProvider, binding.mapView)
        locationOverlay.enableMyLocation()
        binding.mapView.overlays.add(locationOverlay)
        binding.mapView.invalidate()
    }

    /**
     * Инициализация кнопки возврата
     */
    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            findNavController().popBackStack()
        }
    }

    /**
     * Инициализация слушателя изменения типа маршрута, где собирается пакет данных и отправляется в меню навигации
     */
    private fun initRoadListeners(){
        childFragmentManager.setFragmentResultListener(
            MapInfoBottomSheetFragment.ROAD_TYPE_CHANGED,
            viewLifecycleOwner
        ) { requestKey: String?, result: Bundle ->
            Log.d("saas", requestKey.toString())
            model.roadType = result.getString(MapInfoBottomSheetFragment.ROAD_TYPE_CHANGED).toString()
            binding.mapBottomInfo.visibility = View.GONE
            binding.mapBottomRoad.visibility = View.VISIBLE
            if(model.currentMarker == null){
                return@setFragmentResultListener
            }
            model.buildRoadMap(requireContext(), binding, model.currentMarker!!.position, model.roadType)
            model.hospitals?.let {
                model.currentGeoPoint?.distanceToAsDouble(model.currentMarker!!.position)?.let { distance->
                    Log.d("address", model.getCurrentAddress(requireContext(), binding))
                    val bundle = Bundle()
                    bundle.putString("userAddress", model.getCurrentAddress(requireContext(), binding).split(", ")[0])
                    bundle.putDouble("distance", distance/1000.0)
                    bundle.putString("hours",
                        MapUtil.getDistanceTimeAsString(distance/1000.0, model.roadType))

                    val address = if(model.currentHospital is Hospital){
                        (model.currentHospital as Hospital).address
                    } else {
                        (model.currentHospital as HospitalShort).address
                    }
                    bundle.putString("addressHospital", address)
                    bundle.putInt("imageRes", MapUtil.getRoadImageFromFlag(model.roadType))
                    bundle.putString("roadText", MapUtil.getRoadStringFromFlag(requireContext(), model.roadType))
                    childFragmentManager.setFragmentResult(SEND_NAVIGATION_DATA, bundle)
                }
            }
        }
    }

    /**
     * Иницилизация меню навигации
     */
    fun initMapNavigationBackButton(){
        childFragmentManager.setFragmentResultListener(
            MapNavigationBottomSheetFragment.BACK_BUTTON_PRESSED,
            viewLifecycleOwner){_, _ ->
            binding.mapBottomInfo.visibility = View.VISIBLE
            binding.mapBottomRoad.visibility = View.GONE
            model.removeRoad(binding, requireContext())
        }
    }

    companion object{
        const val HOSPITAL_SEND = "HOSPITAL_SEND"
        const val HOSPITAL_SHORT_SEND = "HOSPITAL_SHORT_SEND"
        const val HOSPITAL_NULL_SEND = "HOSPITAL_NULL_SEND"
        const val MARKER_PRESSED_HOSPITAL = "MARKER_PRESSED_HOSPITAL"
        const val MARKER_PRESSERD_HOSPITAL_SHORT = "MARKER_PRESSERD_HOSPITAL_SHORT"
        const val SEND_NAVIGATION_DATA = "SEND_NAVIGATION_DATA"
        const val HIDE_VIEW = "HIDE_VIEW"
    }
}