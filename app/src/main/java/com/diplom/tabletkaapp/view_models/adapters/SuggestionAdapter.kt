package com.diplom.tabletkaapp.view_models.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.room.Room
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.util.DatabaseInfo
import com.diplom.tabletkaapp.view_models.cache.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SuggestionAdapter(context: Context, cursor: Cursor) :
    CursorAdapter(context, cursor, false) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        // Здесь вы можете связать данные с вашим view
        val textView = view.findViewById<TextView>(R.id.suggestion)
        val imageButton = view.findViewById<ImageButton>(R.id.delete_button)
        val text = cursor.getString(cursor.getColumnIndex("column_name"))
        imageButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Room.databaseBuilder(
                    view.context.applicationContext,
                    AppDatabase::class.java,
                    DatabaseInfo.DATABASE_NAME
                ).build().requestDao().getRequests().first()
            }
        }
        textView.text = text
    }
}