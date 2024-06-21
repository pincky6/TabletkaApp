package com.diplom.tabletkaapp.views.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentListBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.ui.search.filter.ListFilterDialogFragment
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.list.adapters.AbstractAdapter
import com.diplom.tabletkaapp.view_models.lists.AbstractModelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Абстрактный класс списка элементов
 */
abstract class AbstractModelList: Fragment() {
    /**
     * Привязка к элементам макета
     */
    protected var binding_: FragmentListBinding? = null
    val binding get() = binding_!!

    /**
     * модель представления списка
     */
    var model: AbstractModelViewModel = AbstractModelViewModel()

    /**
     * Метод для создания базы данных и инициализации элементов интерфейса
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = FragmentListBinding.inflate(inflater, container, false)
        context?.let {
            model.database = Room.databaseBuilder(
                requireContext().applicationContext,
                AppDatabase::class.java,
                DatabaseInfo.DATABASE_NAME
            ).build()

        }
        initBackButton()
        initSearchView()
        initUpdateButton {  }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }

    /**
     * Метод для инициализации списка
     */
    protected fun initRecyclerView(adapter: RecyclerView.Adapter<ViewHolder>){
        if (binding_ == null) return
        binding.recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    /**
     * Метод для инициализации кнопки обновления
     */
    protected fun initUpdateButton(update: () -> Unit){
        if (binding_ == null) return
        binding.updateButton.setOnClickListener{
            update()
        }
    }

    /**
     * Метод для инициализации кнопки фильтрации
     */
    protected fun initFilterButton(filter: () -> Unit){
        if (binding_ == null) return
        binding.filterButton.setOnClickListener{
            filter()
        }
    }

    /**
     * Метод для инициализации кнопки выхожа
     */
    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

    /**
     * Метод для инициализации поисковой строки
     */
    private fun initSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(binding_ == null || binding.recyclerView.adapter == null) return false
                model.listFilter.title = newText
                val list = model.listFilter.filterByTitle(model.modelList)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(list)
                updateUI()
                return false
            }
        })
    }

    /**
     * Метод для получения параметров фильтра, которые выбрал пользователь
     */
    protected fun initGetFilter(listType: Boolean) {
        getParentFragmentManager().setFragmentResultListener(
            ListFilterDialogFragment.LIST_SETTINGS_KEY_ADD, getViewLifecycleOwner()
        ) { _: String?, result: Bundle ->
            model.listFilter.sortMask = result.getInt("sortMask")
            model.listFilter.minPrice = result.getDouble("minPrice")
            model.listFilter.maxPrice = result.getDouble("maxPrice")
            val list = model.listFilter.filter(model.modelList, listType)
            list?.let {
                val lst = model.listFilter.sort(model.listFilter.filter(it, listType), listType)
                (binding.recyclerView.adapter as AbstractAdapter).resetList(lst)
                updateUI()
            }
        }
    }

    /**
     * Метод для инициализации списка в контексте приложения
     */
    protected suspend fun initRecyclerViewWithMainContext(adapter: AbstractAdapter, medicineList: MutableList<AbstractModel>){
        withContext(Dispatchers.Main){
            model.modelList = medicineList
            adapter.resetList(medicineList)
            initRecyclerView(adapter)
        }
    }

    /**
     * Метод по обновлению интерфейса
     */
    fun updateUI(){
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}