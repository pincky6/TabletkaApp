package com.diplom.tabletkaapp.views.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentReductNoteBinding
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase
import com.diplom.tabletkaapp.view_models.notes.NotesReductViewModel

class NotesRedactorFragment: Fragment() {
    var binding_: FragmentReductNoteBinding? = null
    val binding get() = binding_!!
    val model: NotesReductViewModel = NotesReductViewModel()

    /**
     * Инициализация представления
     * Установка параметров названия и описания
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentReductNoteBinding.inflate(inflater, container, false)
        model.note = arguments?.getSerializable("note") as Note
        if(model.note!!.id != "-1") {
            binding.noteTitle.setText(model.note!!.name)
            binding.noteDescribe.setText(model.note!!.describe)
        }
        initBackButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    /**
     * Инициализация кнопки выхода из окна при котором будут сохраняться данные о заметки
     */
    private fun initBackButton(){
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            model.setNote(binding)
            model.note?.let { FirebaseNotesDatabase.add(it) }
            findNavController(binding.root).popBackStack()
        }
    }
    companion object{
        const val NOTE_KEY = "NOTE_KEY"
    }
}