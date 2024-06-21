package com.diplom.tabletkaapp.views.mainmenu

import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.SearchView.OnQueryTextListener
import android.widget.SearchView.OnSuggestionListener
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMainMenuBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseSettingsDatabase
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.util.LocaleHelper
import com.diplom.tabletkaapp.util.UrlStrings
import com.diplom.tabletkaapp.view_models.SettingsViewModel
import com.diplom.tabletkaapp.view_models.adapters.DeletableCustomAdapter
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.RequestDao
import com.diplom.tabletkaapp.viewmodel.adapters.mainmenu.RegionAdapter
import com.diplom.tabletkaapp.viewmodel.parser.RegionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Класс главного меню
 */
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val model: MainMenuViewModel = MainMenuViewModel()

    /**
     * Метод для инициализации базы данных
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            model.database = Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java,
                DatabaseInfo.DATABASE_NAME
            ).build()

        }
    }

    /**
     * Метод для инициализации элементов главного меню
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        initSpinner()
        initSearchView()
        initButtons()

        binding.materialToolbar.isTitleCentered = true
        return binding.root
    }

    /**
     * Инициализация выпадающего списка выбора регионов и настроек приложения
     */
    private fun initSpinner() {
        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val regions = RegionParser.parseRegion()
                val allRegionsString = getString(R.string.all_regions_string)
                val allRegion = regions.find {
                    it.name == allRegionsString
                }
                allRegion?.let {
                    regions.removeAt(regions.indexOf(allRegion))
                    regions.add(0, allRegion)
                }
                model.regionId.let {
                    val selectedRegion = regions.removeAt(it)
                    regions.add(0, selectedRegion)
                }
                withContext(Dispatchers.Main) {
                    if(_binding == null) return@withContext
                    binding.regionsSpinner.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                    binding.regionsSpinner.setPadding(0, 0, 50, 0)
                    binding.regionsSpinner.setPromptId(R.string.select_region_string)
                    binding.regionsSpinner.adapter = RegionAdapter(it, regions)

                    binding.regionsSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                model.regionId = regions[position].id
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    FirebaseSettingsDatabase.readAll(
                        SettingsViewModel(), { settings ->
                        if (settings.languageMode == 0 && Locale.getDefault() != Locale("en")) {
                            LocaleHelper.setLocale(requireContext(), "en")
                            activity?.recreate()
                        } else if (settings.languageMode == 1 && Locale.getDefault() != Locale("be")) {
                            LocaleHelper.setLocale(requireContext(), "be")
                            activity?.recreate()
                        } else if(settings.languageMode == 2 && Locale.getDefault() != Locale("ru")){
                            LocaleHelper.setLocale(requireContext(), "ru")
                            activity?.recreate()
                        } else {

                        }
                        if (settings.themeMode == 0 && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        } else if(settings.themeMode == 1 && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    },

                        {
                        })
                }
            }
        }
    }

    /**
     * Инициалиазация посика лекарственных препаратов
     * Инициализируется список подсказок по введенным символам а также инициализируется перемещение в окно списка аптек
     */
    private fun initSearchView()
    {
        val from = listOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.search_item_text)

        binding.searchMedicines.suggestionsAdapter = DeletableCustomAdapter(requireContext(), R.layout.search_item,
                                        null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,
                                           binding.searchMedicines, object: OnSuggestionListener{
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }
                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = binding.searchMedicines.suggestionsAdapter.getItem(position) as Cursor
                    val columnIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                    val suggestion = cursor.getString(columnIndex)

                    binding.searchMedicines.setQuery(suggestion, true)
                    return true
                }
            })
        binding.searchMedicines.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null) return false
                CoroutineScope(Dispatchers.IO).launch {
                    val requestId = model.addRequestToDatabase(RequestEntity(0, query, 0))
                    withContext(Dispatchers.Main){
                        navigateToMedicineList(query, requestId)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText == null) return false
                CoroutineScope(Dispatchers.IO).launch {
                    model.makeSuggestion(binding, newText)
                }
                return false
            }

        })
    }

    /**
     * Метод по перемещению в список медикаментов
     */
    private fun navigateToMedicineList(query: String, requestId: Long) {
        findNavController(binding.root).navigate(
            MainMenuFragmentDirections.showMedicineModelList(query, requestId, model.regionId)
        )
    }

    /**
     * Метод по настройке меню аптек
     *
     * Инициализируется кнопки по перемещению на сайт, входа в аккаунт, входа в спискок желаний,
     * входа в список краткой информации об аптеках и кнопки истории
     *
     */
    private fun initButtons(){
        binding.moveToSiteButton.setOnClickListener {
            val url = UrlStrings.SITE_REFERENCE
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        binding.enterInAccountButton.setOnClickListener{
            findNavController(binding.root).navigate(
                MainMenuFragmentDirections.showLoginFragment()
            )
        }
        binding.wishListButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                SWITCH_TO_WISH_LIST,
                Bundle())
        }
        binding.pharmacyButton.setOnClickListener {
            findNavController(binding.root).navigate(
                MainMenuFragmentDirections.actionNavigationMainMenuToHospitalRegionFragment()
            )
        }
        binding.historyButton.setOnClickListener {
            findNavController(binding.root).navigate(
                MainMenuFragmentDirections.actionNavigationMainMenuToHistoryFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val SWITCH_TO_WISH_LIST = "SWITCH_TO_WISH_LIST"
    }
}