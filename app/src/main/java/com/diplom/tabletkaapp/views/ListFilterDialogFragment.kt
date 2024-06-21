package com.diplom.tabletkaapp.ui.search.filter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import com.diplom.tabletkaapp.databinding.FragmentListViewSettingsBinding
//import org.osmdroid.util.GeoPoint
/**
 * Класс представления диалога филтра
 */
class ListFilterDialogFragment: DialogFragment() {
    var _binding: FragmentListViewSettingsBinding? = null
    val binding get() = _binding!!
    val listSettings: ListFilterViewModel = ListFilterViewModel()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    /**
     * Получение инициализирующих параметров о минимальной и максимальной цене дистанции, маски сортировки
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListViewSettingsBinding.inflate(inflater, container, false)
        val showDistanceSort = arguments?.getBoolean("showDistanceSort")!!
        //listSettings.userGeoPoint = (arguments?.getSerializable("userGeoPoint") as GeoPoint?)!!
        listSettings.sortMask = arguments?.getInt("sortMask")!!
        listSettings.minPrice = arguments?.getDouble("min_price")!!
        listSettings.maxPrice = arguments?.getDouble("max_price")!!
        if(!showDistanceSort){
            binding.distanceSortCheckBox.visibility = View.GONE
        }
        initTexts()
        initCheckBoxes()
        initButtons()
        return binding.root
    }

    /**
     * Инициаилзация фильтрации по миниамльной и максимальной цене
     */
    private fun initTexts(){
        if(listSettings.minPrice == 0.0 &&
            listSettings.maxPrice == 0.0){
            return
        }
        if(listSettings.minPrice != Double.MIN_VALUE)
            binding.minPrice.setText(listSettings.minPrice.toString())
        if(listSettings.maxPrice != Double.MAX_VALUE)
            binding.maxPrice.setText(listSettings.maxPrice.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Инициализация чекбоксов для установления сортировочной маски
     */
    private fun initCheckBoxes(){
        binding.sortByMinPriceCheckBox.isChecked = (listSettings.sortMask and 1) == 1
        binding.sortByTitleCheckBox.isChecked = (listSettings.sortMask and 2) == 2
        binding.sortByCountCheckBox.isChecked = (listSettings.sortMask and 4) == 4
        binding.distanceSortCheckBox.isChecked = (listSettings.sortMask and 8) == 8
        binding.sortByMinPriceCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listSettings.sortMask = listSettings.sortMask or 1
            } else {
                val maskToClear = 1
                listSettings.sortMask = listSettings.sortMask and maskToClear.inv()
            }
        }
        binding.sortByTitleCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                listSettings.sortMask = listSettings.sortMask or 2
            } else {
                val maskToClear = 2
                listSettings.sortMask = listSettings.sortMask and maskToClear.inv()
            }
        }
        binding.sortByCountCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                listSettings.sortMask = listSettings.sortMask or 4
            } else {
                val maskToClear = 4
                listSettings.sortMask = listSettings.sortMask and maskToClear.inv()
            }
        }
        binding.distanceSortCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listSettings.sortMask = listSettings.sortMask or 8
            } else {
                val maskToClear = 8
                listSettings.sortMask = listSettings.sortMask and maskToClear.inv()
            }
        }
    }

    /**
     * Инициализация кнопок закрытия и передачи параметров в вызывабщее окно
     */
    private fun initButtons(){
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.okButton.setOnClickListener {
            val bundle = Bundle()
            if(binding.minPrice.text.isEmpty()){
                listSettings.minPrice = Double.MIN_VALUE
            } else {
                listSettings.minPrice = binding.minPrice.text.toString().toDouble()
            }
            if(binding.maxPrice.text.isEmpty()) {
                listSettings.maxPrice = Double.MAX_VALUE
            } else {
                listSettings.maxPrice = binding.maxPrice.text.toString().toDouble()
            }
            bundle.putInt("sortMask", listSettings.sortMask)
            bundle.putDouble("minPrice", listSettings.minPrice)
            bundle.putDouble("maxPrice", listSettings.maxPrice)
            getParentFragmentManager().setFragmentResult(LIST_SETTINGS_KEY_ADD, bundle);
            dismiss()
        }
    }

    companion object KEYS{
        const val LIST_SETTINGS_KEY_ADD = "LIST_SETTINGS_KEY_ADD"
        const val LIST_SETTINGS_KEY_GET = "LIST_SETTINGS_KEY_GET"
    }
}