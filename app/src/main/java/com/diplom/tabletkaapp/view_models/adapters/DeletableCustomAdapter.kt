package com.diplom.tabletkaapp.view_models.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.lang.reflect.Array;

class DeletableCustomAdapter (
        context:Context,
        layout: Int,
        c:Cursor?,
        from: List<String>,
        to: IntArray,
        flags: Int,
        var searchView: SearchView
) : SimpleCursorAdapter(context, layout, c, from.toTypedArray(), to, flags) {

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        super.bindView(view, context, cursor)
        val button = view.findViewById<ImageButton>(R.id.delete_button)
        button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Room.databaseBuilder(
                    view.context.applicationContext,
                    AppDatabase::class.java,
                    DatabaseInfo.DATABASE_NAME
                ).build().requestDao().deleteByRequest(view.findViewById<TextView>(R.id.search_item_text).text.toString())
                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                    searchView.clearFocus()
                }
            }
        }
    }
}