package com.diplom.tabletkaapp.views.mainmenu

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import androidx.lifecycle.ViewModel
import com.diplom.tabletkaapp.databinding.FragmentMainMenuBinding
import com.diplom.tabletkaapp.models.cache_data_models.RequestEntity
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import com.diplom.tabletkaapp.view_models.cache.RequestDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MainMenuViewModel : ViewModel() {

    var database: AppDatabase? = null
    var regionId: Int = -1
    fun addRequestToDatabase(request: RequestEntity): Long{
        var requestId = 0L
        database?.let {
            val requestDao: RequestDao = it.requestDao()
            if(requestDao.getRequestsLikeRequest(request.request).isEmpty()) {
                requestId = requestDao.insertRequest(request)
            }
        }
        return requestId
    }

    suspend fun makeSuggestion(binding: FragmentMainMenuBinding, text: String) {
        val cursor = MatrixCursor(
            arrayOf(
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1
            )
        )
        var suggestions: MutableList<RequestEntity?>
        database?.requestDao()?.getRequests()?.collect {
            suggestions = it.toMutableList()
            suggestions.forEachIndexed { index, suggestion ->
                if (suggestion?.request?.contains(text, true) ?: true) {
                    if (suggestion != null) {
                        cursor.addRow(arrayOf(index, suggestion.request))
                    }
                }
            }
            withContext(Dispatchers.Main) {
                binding.searchMedicines.suggestionsAdapter.changeCursor(cursor)
            }
        }
    }
}