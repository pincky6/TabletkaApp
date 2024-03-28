package com.diplom.tabletkaapp.ui.search.filter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.diplom.tabletkaapp.databinding.FragmentListViewSettingsBinding

class ListSettingsDialogFragment: DialogFragment() {
    var _binding: FragmentListViewSettingsBinding? = null
    val binding get() = _binding!!
    val listSettings: ListSettingsViewModel = ListSettingsViewModel()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListViewSettingsBinding.inflate(inflater, container, false)
        listSettings.sortMask = arguments?.getInt("checkBoxMask")!!
        listSettings.minPrice = arguments?.getDouble("min_price")!!
        listSettings.maxPrice = arguments?.getDouble("max_price")!!
        setCheckBoxes()
        initButtons()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCheckBoxes(){
        binding.sortByMinPriceCheckBox.isChecked = (listSettings.sortMask and 1) == 1
        binding.distanceSortCheckBox.isChecked = (listSettings.sortMask and 2) == 2
    }
    private fun initButtons(){
        binding.cancelButton.setOnClickListener { findNavController().popBackStack() }
        binding.okButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("sortMask", listSettings.sortMask)
            bundle.putDouble("minPrice", listSettings.minPrice)
            bundle.putDouble("maxPrice", listSettings.maxPrice)
            getParentFragmentManager().setFragmentResult(LIST_SETTINGS_KEY_ADD, bundle);
        }
    }

    companion object KEYS{
        const val LIST_SETTINGS_KEY_ADD = "LIST_SETTINGS_KEY_ADD"
        const val LIST_SETTINGS_KEY_GET = "LIST_SETTINGS_KEY_GET"
    }
}