package com.diplom.tabletkaapp.view_models.map

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapViewModel: ViewModel() {
    lateinit var geoPointsList: GeoPointsList
    fun setMarker(context: Context, binding: FragmentMapBinding, geoPoint: GeoPoint){
                val marker = Marker(binding.mapView)
                marker.position = geoPoint
                marker.icon = ContextCompat.getDrawable(context, R.drawable.baseline_location_on_24)
//                marker.title = name
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
}