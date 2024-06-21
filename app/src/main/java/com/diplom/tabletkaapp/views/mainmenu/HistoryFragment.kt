package com.diplom.tabletkaapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.HistoryFragmentBinding
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.adapters.HistoryAdapter
import com.diplom.tabletkaapp.view_models.adapters.NotesAdapter
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Класс списка истории
 */
class HistoryFragment: Fragment() {
    var binding_: HistoryFragmentBinding? = null
    val binding get() = binding_!!
    var list: MutableList<RequestEntity> = mutableListOf()
    lateinit var database: AppDatabase

    /**
     * Метод по инициализации базы данных
     * списка истории и поисковой строки
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = Room.databaseBuilder(
                requireContext().applicationContext,
                AppDatabase::class.java,
                DatabaseInfo.DATABASE_NAME
                ).build()
        binding_ = HistoryFragmentBinding.inflate(inflater, container, false)
        initBackButton()
        CoroutineScope(Dispatchers.IO).launch{
            list = database.requestDao().getRequests().first().toMutableList()
            withContext(Dispatchers.Main) {
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = HistoryAdapter(list) {
                    updateUI()
                }
            }
        }
        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null) return false

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(binding_ == null || binding.recyclerView.adapter == null) return false
                if(newText.isEmpty()){
                    (binding.recyclerView.adapter as HistoryAdapter).resetList(list)
                }
                val regex = Regex(newText)
                (binding.recyclerView.adapter as HistoryAdapter).resetList(list.filter{ item: RequestEntity ->
                    regex.findAll(
                        item.request.lowercase(),
                        0
                    ).iterator().hasNext()
                } as MutableList<RequestEntity>)
                return false
            }

        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Метод для обновления UI
     */
    fun updateUI(){
        CoroutineScope(Dispatchers.IO).launch{
            list = database.requestDao().getRequests().first().toMutableList()
            withContext(Dispatchers.Main) {
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = HistoryAdapter(list) {
                    updateUI()
                }
            }
        }
    }
    /**
     * Инициализация кнопки выхода из окна
     */
    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

}