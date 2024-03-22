package com.diplom.tabletkaapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.databinding.FragmentMapBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class MapFragment: Fragment() {
    private var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapVew.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = binding.mapView.controller
    }
}