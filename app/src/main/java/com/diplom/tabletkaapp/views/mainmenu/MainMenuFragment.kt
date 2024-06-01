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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentMainMenuBinding
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.util.UrlStrings
import com.diplom.tabletkaapp.view_models.adapters.SuggestionAdapter
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.RequestDao
import com.diplom.tabletkaapp.viewmodel.adapters.mainmenu.RegionAdapter
import com.diplom.tabletkaapp.viewmodel.parser.RegionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val model: MainMenuViewModel = MainMenuViewModel()

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
                }
            }
        }
    }
    private fun initSearchView()
    {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.layout.search_item)

        binding.searchMedicines.suggestionsAdapter = SuggestionAdapter(model.database.requestDao().getRequests().first())
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
        binding.searchMedicines.setOnSuggestionListener(object: OnSuggestionListener{
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
    }
    private fun navigateToMedicineList(query: String, requestId: Long) {
        findNavController(binding.root).navigate(
            MainMenuFragmentDirections.showMedicineModelList(query, requestId, model.regionId)
        )
    }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val SWITCH_TO_WISH_LIST = "SWITCH_TO_WISH_LIST"
    }
}