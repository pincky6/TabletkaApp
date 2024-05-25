package com.diplom.tabletkaapp.views.dashboard

import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView.OnQueryTextListener
import android.widget.SearchView.OnSuggestionListener
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentDashboardBinding
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.util.UrlStrings
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.RequestDao
import com.diplom.tabletkaapp.viewmodel.adapters.mainmenu.RegionAdapter
import com.diplom.tabletkaapp.viewmodel.parser.RegionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            appDatabase = Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java,
                "cache"
            ).build()

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initSpinner()
        initSearchView()
        initButtons()
        return root
    }

    private fun initSpinner(){
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
                withContext(Dispatchers.Main){
                    binding.regionsSpinner.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                    binding.regionsSpinner.setPadding(0, 0, 50, 0)
                    binding.regionsSpinner.setPromptId(R.string.select_region_string)
                    binding.regionsSpinner.adapter = RegionAdapter(it, regions)
                }
            }
        }
    }
    private fun initSearchView()
    {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.search_item)

        binding.searchMedicines.suggestionsAdapter = SimpleCursorAdapter(context, R.layout.search_item,
                                        null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        binding.searchMedicines.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(context, "oaoaoa", Toast.LENGTH_SHORT).show()
                query?.let {subString ->
                    CoroutineScope(Dispatchers.IO).launch {
                        context?.let {
                            val requestDao: RequestDao = appDatabase.requestDao()
                            requestDao.getRequestsLikeRequest(subString).collect{requestEntities ->
                                if(requestEntities.isEmpty()) {
                                    requestDao.insertRequest(RequestEntity(0, subString, 0))
                                }
                            }
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        val cursor = MatrixCursor(arrayOf(BaseColumns._ID,
                            SearchManager.SUGGEST_COLUMN_TEXT_1))
                        var suggestions: MutableList<RequestEntity?>
                        appDatabase.requestDao().getRequests().collect{
                            suggestions = it.toMutableList()
                            suggestions.forEachIndexed { index, suggestion ->
                                if (suggestion?.request?.contains(newText, true) ?: true) {
                                    if (suggestion != null) {
                                        cursor.addRow(arrayOf(index, suggestion.request))
                                    }
                                }
                            }
                            withContext(Dispatchers.Main){
                                binding.searchMedicines.suggestionsAdapter.changeCursor(cursor)
                            }
                        }
                    }
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
    private fun initButtons(){
        binding.moveToSiteButton.setOnClickListener {
            val url = UrlStrings.SITE_REFERENCE
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        binding.enterInAccountButton.setOnClickListener{
            findNavController(binding.root).navigate(
                DashboardFragmentDirections.showLoginFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}