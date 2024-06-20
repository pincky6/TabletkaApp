package com.diplom.tabletkaapp.viewmodel.adapters.mainmenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.diplom.tabletkaapp.models.Region

/**
 * Класс который отвечает за представление регионов
 */
class RegionAdapter(context: Context, regions: List<Region>) : ArrayAdapter<Region>(context, 0, regions) {
    /**
     * Метод который создает элемент представления региона
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val region = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = region?.name
        return view
    }

    /**
     * Метод который возвращает нажатый элемент в соотвествии с позицией и устанавливает его имя
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val region = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = region?.name
        return view
    }
}