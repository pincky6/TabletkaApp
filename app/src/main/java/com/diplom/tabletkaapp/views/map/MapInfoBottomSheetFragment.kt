package com.diplom.tabletkaapp.views.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMapBottomSheetBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.HospitalShort
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.view_models.map.MapBottomSheetViewModel
import models.Hospital
import org.osmdroid.bonuspack.routing.OSRMRoadManager

/**
 * Панель информации об выбранной аптеке
 */
class MapInfoBottomSheetFragment: Fragment() {
    var _binding: FragmentMapBottomSheetBinding? = null
    val binding get() = _binding!!
    val model = MapBottomSheetViewModel()

    companion object{
        const val REQUEST_HOSPITAL = "REQUEST_HOSPITAL"
        const val ROAD_TYPE_CHANGED = "ROAD_TYPE_CLICKED"
    }

    /**
     * Метод по инициализации панели навигации. Инициализируется добавление в список желаний
     * Скрываются все не нужные элементы
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBottomSheetBinding.inflate(inflater, container, false)

        binding.hospitalInfoPanel.showGeolocationButton.visibility = View.GONE
        binding.hospitalInfoPanel.pricesRecyclerView.visibility = View.GONE
        binding.hospitalInfoPanel.showMedicineInfoButton.visibility = View.GONE
        binding.hospitalInfoPanel.hospitalWishButton
        initWishButton()
        binding.toogleButton.setOnClickListener {
            if (binding.root.height == binding.toogleButton.layoutParams.height) {
                expandPanel()
            } else {
                collapsePanel()
            }
        }
        setInfoContent()
        return binding.root
    }

    /**
     * Инициализация кнопок, слушателей и настройка видимости информации о типе дорог
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initHospitalResultListener()
        setVisibilityRoadPart(View.GONE)
        parentFragmentManager.setFragmentResult(REQUEST_HOSPITAL, Bundle())
    }

    /**
     * Инициализация кнопки списка желаемого
     */
    private fun initWishButton(){
        binding.hospitalInfoPanel.hospitalWishButton.setImageResource(
            if(model.hospital?.wish ?: false) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.hospitalInfoPanel.hospitalWishButton.setOnClickListener {
            if(!FirebaseSingInRepository.checkUserExistWithWarningDialog(binding.root.context)){
                return@setOnClickListener
            }
            model.hospital?.wish = !model.hospital?.wish!!
            model.hospital?.let {
                if(it.wish){
                    FirebaseHospitalDatabase.add(it)
                    binding.hospitalInfoPanel.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    FirebaseHospitalDatabase.delete(it)
                    binding.hospitalInfoPanel.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
    }

    /**
     * Вывести информацию об апеке
     */
    fun setHospital(newHospital: Hospital){
        model.hospital = newHospital
        binding.hospitalInfoPanel.name.text = newHospital.name
        binding.hospitalInfoPanel.address.text = newHospital.address
        binding.hospitalInfoPanel.phone.text = newHospital.phone
        binding.hospitalInfoPanel.root.visibility = View.VISIBLE
    }

    /**
     * Вывести краткую инфу об аптеке
     */
    fun setHospitalShort(newHospital: HospitalShort){
        model.hospital = newHospital
        if(_binding == null) return
        binding.hospitalInfoPanel.name.text = newHospital.name
        binding.hospitalInfoPanel.address.text = newHospital.address
        binding.hospitalInfoPanel.phone.text = newHospital.phone
        binding.hospitalInfoPanel.root.visibility = View.VISIBLE
        binding.hospitalInfoPanel.hospitalWishButton.visibility = View.GONE
        //setInfoContent()
    }

    /**
     * Установка текста
     */
    private fun setInfoContent(){
        model.hospital?.let {
            if(_binding == null) return
            binding.hospitalInfoPanel.name.text = if(model.hospital is Hospital){
                (model.hospital as Hospital).name
            } else {
                (model.hospital as HospitalShort).name
            }
            binding.hospitalInfoPanel.address.text = if(model.hospital is Hospital){
                (model.hospital as Hospital).address
            } else {
                (model.hospital as HospitalShort).address
            }
            binding.hospitalInfoPanel.phone.text = if(model.hospital is Hospital){
                (model.hospital as Hospital).phone
            } else {
                (model.hospital as HospitalShort).phone
            }
        }

    }

    /**
     * Прячется бесполезный ui
     */
    fun hideHospitalInfo(){
        if(_binding != null) {
            binding.hospitalInfoPanel.root.visibility = View.GONE
        }
    }

    /**
     * Показ панели информации
     */
    private fun expandPanel() {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                binding.toogleButton.setImageResource(R.drawable.baseline_expand_less_24)
                binding.root.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                binding.root.requestLayout()
            }
        }
        animation.duration = 300
        binding.hospitalInfoPanel.root.visibility = View.VISIBLE
        binding.root.startAnimation(animation)
    }

    /**
     * Скрытие панели информации
     */
    private fun collapsePanel() {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)

                binding.toogleButton.setImageResource(R.drawable.baseline_expand_more_24)
                binding.root.layoutParams.height = binding.toogleButton.layoutParams.height
                binding.root.requestLayout()
            }
        }
        animation.duration = 300
        binding.hospitalInfoPanel.root.visibility = View.GONE
        binding.root.startAnimation(animation)
    }

    /**
     * Инициализация кнопок начала маршрута
     */
    private fun initButtons(){
        _binding?.byCarButton?.setOnClickListener {
            sendMessage(OSRMRoadManager.MEAN_BY_CAR)
        }
        _binding?.byFootButton?.setOnClickListener {
            sendMessage(OSRMRoadManager.MEAN_BY_FOOT)
        }
        _binding?.byVeloButton?.setOnClickListener {
            sendMessage(OSRMRoadManager.MEAN_BY_BIKE)
        }
    }

    /**
     * Отправка сообщения родительскому представлению
     */
    private fun sendMessage(message: String){
        val bundle = Bundle()
        bundle.putString(ROAD_TYPE_CHANGED, message)
        Log.d("PARENT FRAGMENT", parentFragment.toString())
        parentFragmentManager.setFragmentResult(ROAD_TYPE_CHANGED, bundle)
    }

    /**
     * Инициализация слушателей
     */
    private fun initHospitalResultListener(){
        parentFragmentManager.setFragmentResultListener(
            MapFragment.HOSPITAL_NULL_SEND,
            viewLifecycleOwner){_, _ ->
            hideHospitalInfo()
        }
        parentFragmentManager.setFragmentResultListener(
            MapFragment.HOSPITAL_SEND,
            viewLifecycleOwner){_, bundle ->
            setHospital(bundle.getSerializable(MapFragment.HOSPITAL_SEND) as Hospital)
        }
        parentFragmentManager.setFragmentResultListener(
            MapFragment.HOSPITAL_SHORT_SEND,
            viewLifecycleOwner){_, bundle ->
            setHospitalShort(bundle.getSerializable(MapFragment.HOSPITAL_SHORT_SEND) as HospitalShort)
        }
        parentFragmentManager.setFragmentResultListener(
            MapFragment.MARKER_PRESSED_HOSPITAL,
            viewLifecycleOwner){_, bundle ->
            setHospital(bundle.getSerializable(MapFragment.MARKER_PRESSED_HOSPITAL) as Hospital)
            setVisibilityRoadPart(View.VISIBLE);
        }
        parentFragmentManager.setFragmentResultListener(
            MapFragment.MARKER_PRESSERD_HOSPITAL_SHORT,
            viewLifecycleOwner){_, bundle ->
            val hospitalShort = bundle.getSerializable(MapFragment.MARKER_PRESSERD_HOSPITAL_SHORT) as HospitalShort
            setHospitalShort(hospitalShort)
            setVisibilityRoadPart(View.VISIBLE);
        }
        parentFragmentManager.setFragmentResultListener(
            MapFragment.HIDE_VIEW,
            viewLifecycleOwner
        ){_, _ ->
            binding.hospitalInfoPanel.root.visibility = View.GONE
        }
    }

    /**
     * Установка видимости необходимых элементов интерфейса
     */
    private fun setVisibilityRoadPart(visibilityFlag: Int){
        binding.byVeloButton.visibility = visibilityFlag
        binding.byCarButton.visibility = visibilityFlag
        binding.byFootButton.visibility = visibilityFlag
        binding.textView.visibility = visibilityFlag
    }
}