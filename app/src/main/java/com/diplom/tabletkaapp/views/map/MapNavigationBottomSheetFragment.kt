package com.diplom.tabletkaapp.views.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapNavigationSheetBinding
import com.diplom.tabletkaapp.util.MapUtil
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import java.util.Locale

/**
 * Класс панели навигации
 */
class MapNavigationBottomSheetFragment: Fragment() {
    var binding_: FragmentMapNavigationSheetBinding? = null
    val binding get() = binding_!!

    /**
     * Инициализация элементов управления
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentMapNavigationSheetBinding.inflate(inflater, container, false)
        initBackButton()
        initParentFragmentListener()
        return binding.root
    }

    /**
     * Возврат на панель информации
     */
    private fun initBackButton(){
        binding_?.backButton?.setOnClickListener {
            parentFragmentManager.setFragmentResult(BACK_BUTTON_PRESSED, Bundle())
        }
    }

    /**
     * Инициализация слушателя получения инфы об аптеке
     */
    private fun initParentFragmentListener(){
        parentFragmentManager.setFragmentResultListener(
            MapFragment.SEND_NAVIGATION_DATA,
            viewLifecycleOwner){_, bundle ->
            binding.roadTypeText.text = bundle.getString("roadText")
            binding.timeAndWayText.text = String.format(Locale.US, "%.2f", bundle.getDouble("distance")) +
                     " ${getString(R.string.kilometers)}(${bundle.getString("hours")})"
            binding.imageView.setImageResource(bundle.getInt("imageRes"))
            binding.fromAddressText.text = bundle.getString("userAddress")
            binding.toAddressText.text = bundle.getString("addressHospital")
        }
    }

    companion object{
        const val BACK_BUTTON_PRESSED = "BACK_BUTTON_PRESSED"
    }
}