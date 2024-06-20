package com.diplom.tabletkaapp.ui.search.holders

import android.view.View
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.ItemHospitalBinding
import com.diplom.tabletkaapp.firebase.authentication.FirebaseSingInRepository
import com.diplom.tabletkaapp.firebase.database.FirebaseHospitalDatabase
import com.diplom.tabletkaapp.models.data_models.GeoPointsList
import com.diplom.tabletkaapp.models.data_models.HospitalsList
import com.diplom.tabletkaapp.ui.search.adapters.MedicineInfoAdapter
import com.diplom.tabletkaapp.views.lists.simple_lists.HospitalModelListDirections
import com.diplom.tabletkaapp.views.wishlists.lists.HospitalWishListFragmentDirections

import models.Hospital
import org.osmdroid.util.GeoPoint

/**
 * Класс представления элемента списка аптек
 * @param binding привязка к элементам макета
 * @param show флаг показа информации о медикаменте
 * @param isWish флаг обозначающий находится для данный элемент в списке желаемого
 */
class HospitalHolder(
    var binding: ItemHospitalBinding,
    var show: Boolean,
    val isWish: Boolean
): RecyclerView.ViewHolder(binding.root) {
    /**
     * Привязка модели к представлению
     * Присваивается текст и инициализируются кнопки карты, списка желаемого и отображения списка инфы об медикаменте
     * @param hospital аптека
     * @param regionId идентификатор региона
     * @param medicineId идентификатор препарата
     * @param requestId идентификатор запроса
     * @param query запрос
     * @param onWishListClicked функция вызывающаяся при нажатии на кнопку списка желаний, если флаг isWish == true
     */
    fun bind(hospital: Hospital,
             regionId: Int, medicineId: Long,
             requestId: Long, query: String,
             onWishListClicked: ((Boolean)->Unit)?){
        show = false
        binding.name.text = hospital.name
        binding.address.text = hospital.address
        binding.phone.text = hospital.phone
        binding.pricesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.pricesRecyclerView.adapter = MedicineInfoAdapter(hospital)
        binding.pricesRecyclerView.visibility = View.GONE
        initMapButton(hospital)
        initWishButton(hospital, requestId, regionId, query, onWishListClicked)
        initShowMedicineButton(hospital)
    }

    /**
     * Инициализация кнопки показа информации о медикаменте
     * @param hospital аптека
     */
    private fun initShowMedicineButton(hospital: Hospital) {
        binding.showMedicineInfoButton.setOnClickListener { v ->
            if (!show) {
                setPharmacyPriceVisibility(R.drawable.baseline_expand_more_24, hospital,
                        View.VISIBLE, true)
            } else {
                setPharmacyPriceVisibility(R.drawable.baseline_expand_less_24, null,
                    View.INVISIBLE, false)
            }
        }
    }

    /**
     * Инициализация кнопки списка желаемого
     * Если элемент добавлен, то он удаляется из списка и наоборот
     * @param hospital аптека
     * @param requestId идентификатор запроса
     * @param regionId идентификатор региона
     * @param query запрос
     * @param onWishListClicked функция вызывающаяся при нажатии на кнопку списка желаний, если флаг isWish == true
     */
    private fun initWishButton(hospital: Hospital, requestId: Long,
                               regionId: Int, query: String,
                               onWishListClicked: ((Boolean)->Unit)?){
        binding.hospitalWishButton.setImageResource(
            if(hospital.wish) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            })
        binding.hospitalWishButton.setOnClickListener {
            if(!FirebaseSingInRepository.checkUserExistWithWarningDialog(binding.root.context)){
                return@setOnClickListener
            }
            hospital.wish = !hospital.wish
            if(hospital.wish){
                if(isWish){
                    FirebaseHospitalDatabase.add(hospital)
                } else {
                    FirebaseHospitalDatabase.add(hospital, requestId, regionId, query)
                }
                binding.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                if(isWish) {
                    FirebaseHospitalDatabase.delete(hospital)
                } else {
                    FirebaseHospitalDatabase.delete(hospital, requestId, regionId, query)
                }
                binding.hospitalWishButton.setImageResource(android.R.drawable.btn_star_big_off)
            }
            onWishListClicked?.invoke(hospital.wish)
        }
    }

    /**
     * Инициализация кнопки карт
     * При нажатии на нее происходит перемещение в модуль карт
     */
    private fun initMapButton(hospital: Hospital){
            binding.showGeolocationButton.setOnClickListener {
                val geoPointsList = GeoPointsList(mutableListOf(GeoPoint(hospital.latitude, hospital.longitude)))
                val hospitals = HospitalsList(mutableListOf(hospital))
                findNavController(binding.root).navigate(
                    if(isWish) HospitalWishListFragmentDirections.actionHospitalWishListFragmentToMapFragment(geoPointsList, hospitals)
                    else HospitalModelListDirections.actionHospitalModelListToMapFragment(geoPointsList, hospitals)
                )
            }
    }

    /**
     * Устанавливает видимость списка информации о искомом лекарстве
     */
    private fun setPharmacyPriceVisibility(imageResource: Int, hospital: Hospital?,
                                           visibleFlag: Int, flag: Boolean){
        binding.showMedicineInfoButton.setImageResource(imageResource)
        (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).setPharmacy(hospital)
        (binding.pricesRecyclerView.adapter as MedicineInfoAdapter).notifyDataSetChanged()
        binding.pricesRecyclerView.visibility = visibleFlag
        show = flag
    }
}