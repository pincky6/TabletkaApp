package com.diplom.tabletkaapp.views.lists.simple_lists

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.parser.HospitalParser
import com.diplom.tabletkaapp.util.CacheHospitalConverter
import com.diplom.tabletkaapp.util.ComparatorUtil
import com.diplom.tabletkaapp.view_models.lists.HospitalModelListViewModel
import com.diplom.tabletkaapp.view_models.cache.HospitalCacher
import com.diplom.tabletkaapp.view_models.list.adapters.HospitalAdapter
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Hospital
import models.Medicine
import org.osmdroid.util.GeoPoint

class HospitalModelList:
AbstractModelList() {
    val hospitalModel = HospitalModelListViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initGetFilter(true)

        hideUselessUI()
        initFloatingButton()
        val query = arguments?.getString("query") ?: ""
        val requestId = arguments?.getInt("requestId")?.toLong() ?: 0
        val regionId = arguments?.getInt("regionId") ?: 0
        hospitalModel.medicine = arguments?.getSerializable("medicine") as Medicine
        initMedicineInfo()
        initWishButton(hospitalModel.medicine, regionId, requestId, query)
        CoroutineScope(Dispatchers.IO).launch {
            val hospitalEntities = model.database.hospitalDao().getHospitalsByRegionIdAndMedicineIdAndRecordId(regionId, hospitalModel.medicine.id.toLong(),
                                                                                                               requestId)
            var maxPage = model.database.hospitalDao().getMaxPage(requestId, regionId, hospitalModel.medicine.id.toLong())
            val convertedList = CacheHospitalConverter.fromEntityListToModelList(hospitalEntities)
            setWish(convertedList, maxPage, query, regionId, requestId)
            val hospitalList = mutableListOf<AbstractModel>()
            if(maxPage == 0) maxPage++
            for(i in 0 until maxPage) {
                hospitalList.addAll(
                    HospitalParser.parsePageFromName(
                        hospitalModel.medicine.medicineReference,
                        regionId,
                        i + 1
                    )
                )
            }
            HospitalCacher.validateMedicineDatabase(model.database, regionId, hospitalModel.medicine.id.toLong(), requestId,
                hospitalList, hospitalEntities)
            setWish(hospitalList, maxPage, query, regionId, requestId)
        }
        binding.filterButton.text = context?.getString(R.string.hospital_filter_and_sort_button)
        initFilterButton()
        return binding.root
    }

    private fun initFilterButton(){
        initFilterButton {
            Navigation.findNavController(binding.root).navigate(
                HospitalModelListDirections.showListFilterDialogFragmentHospital(true,
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.sortMask)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun hideUselessUI(){
        binding.updateButton.visibility = View.GONE
    }

    private fun initMedicineInfo(){
        binding.medicineTitle.text = hospitalModel.medicine.name
        binding.medicineRecipe.text = hospitalModel.medicine.recipe
        binding.medicineCountry.text = hospitalModel.medicine.country
        binding.copyButton.setOnClickListener{
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("����������", hospitalModel.medicine.toString())
            clipboard.setPrimaryClip(clip)
        }
        binding.infoButton.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(
                HospitalModelListDirections.showInfoFragment(hospitalModel.medicine)
            )
        }
    }
    private fun initWishButton(medicine: Medicine, regionId: Int, requestId: Long, query: String){
        binding.wishButton.setImageResource(
            if(medicine.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.wishButton.setOnClickListener {
            if(!FirebaseSingInRepository.checkUserExistWithWarningDialog(binding.root.context)){
                return@setOnClickListener
            }
            medicine.wish = !medicine.wish
            if(medicine.wish){
                FirebaseMedicineDatabase.add(hospitalModel.medicine, requestId, regionId, query)
                binding.wishButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                FirebaseMedicineDatabase.delete(hospitalModel.medicine, requestId, regionId, query)
                binding.wishButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
    }

    private fun initFloatingButton(){
        binding.floatingActionButton.setOnClickListener {
            val geoPointsList = GeoPointsList(model.modelList.flatMap {
                val hospital = (it as Hospital)
                mutableListOf(GeoPoint(hospital.latitude, hospital.longitude))
            } as MutableList<GeoPoint>)

            Navigation.findNavController(binding.root).navigate(
                HospitalModelListDirections.actionHospitalModelListToMapFragment(geoPointsList, HospitalsList(model.modelList))
            )
        }
    }
    private fun setWish(hospitalList: MutableList<AbstractModel>, maxPage: Int, query: String, regionId: Int, requestId: Long){
        FirebaseHospitalDatabase.readAll(
            mutableListOf(), object: OnCompleteListener{
                override fun complete(list: MutableList<AbstractModel>) {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (element in list){
                            val findElement = hospitalList.find{ComparatorUtil.compare(it as Hospital, element as Hospital)}
                            if(findElement != null){
                                findElement.wish = true
                            }
                        }
                        withContext(Dispatchers.Main){
                            initRecyclerViewWithMainContext(HospitalAdapter(hospitalList, model.database, maxPage, query, regionId,
                                hospitalModel.medicine,
                                hospitalModel.medicine.id.toLong(), requestId, null), hospitalList)
                        }
                    }
                }

            },
            object: OnReadCancelled{
                override fun cancel() {
                }

            })
    }
}