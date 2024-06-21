package com.diplom.tabletkaapp.views.wishlists.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.firebase.database.FirebaseMedicineDatabase
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.view_models.adapters.lists.WishListAdapter
import com.diplom.tabletkaapp.view_models.wish_lists.WishListViewModel
import com.diplom.tabletkaapp.views.lists.AbstractModelList
import com.diplom.tabletkaapp.views.lists.simple_lists.HospitalModelListDirections
import com.diplom.tabletkaapp.views.wishlists.lists.MedicineWishListFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Представление желаемых медикаментов
 */
class MedicineWishListFragment: AbstractModelList() {
    val wishModel: WishListViewModel = WishListViewModel()
    /**
     * Иницилиазция представления
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        hideUselessUI()
        initUpdateButton()
        initGetFilter(false)
        wishModel.database = FirebaseMedicineDatabase
        initFirebaseRecylcerView()
        binding.filterButton.text = context?.getString(R.string.hospital_filter_and_sort_button)
        initFilterButton()
        return binding.root
    }
    /**
     * Инициализация списка желаемого
     */
    private fun initFirebaseRecylcerView(){
        wishModel.loadFromDatabase(object : OnCompleteListener {
            override fun complete(list: MutableList<AbstractModel>) {
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = WishListAdapter(list){
                    loadFromFirebase()
                }
                updateFirebaseUI(list)
            }
        },
            object : OnReadCancelled {
                override fun cancel() {

                }

            })
    }
    /**
     * Инициализация кнопки обновления
     */
    private fun initUpdateButton(){
        binding.updateButton.setText(getText(R.string.update_wish_list))
        binding.updateButton.setOnClickListener {
            loadFromFirebase()
        }
    }
    /**
     * Инициализация кнопки перемещения в фильтр
     */
    private fun initFilterButton(){
        initFilterButton {
            Navigation.findNavController(binding.root).navigate(
                MedicineWishListFragmentDirections.showListFilterDialogFragmentMedicineWish(false,
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.minPrice.toFloat(),
                    model.listFilter.sortMask)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Прячем бесполезный UI
     */
    private fun hideUselessUI(){
        binding.medicineInfo.visibility = View.GONE
        binding.floatingActionButton.visibility = View.GONE
    }
    /**
     * Загрузка аптек из Firebase
     */
    private fun loadFromFirebase(){
        wishModel.list.clear()
        wishModel.loadFromDatabase(object : OnCompleteListener {
            override fun complete(list: MutableList<AbstractModel>) {
                updateFirebaseUI(list)
            }
        },
            object : OnReadCancelled {
                override fun cancel() {

                }

            })
    }
    /**
     * Обновление списка желаний аптек
     */
    fun updateFirebaseUI(list: MutableList<AbstractModel>){
        model.modelList = list
        if(binding != null && binding.recyclerView.adapter != null){
            (binding.recyclerView.adapter as WishListAdapter).resetList(list)
            (binding.recyclerView.adapter as WishListAdapter).notifyDataSetChanged()
        }
    }
}