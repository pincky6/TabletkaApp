package com.diplom.tabletkaapp.views.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.diplom.tabletkaapp.R
import com.diplom.tabletkaapp.databinding.FragmentNoteListBinding
import com.diplom.tabletkaapp.firebase.database.FirebaseSettingsDatabase
import com.diplom.tabletkaapp.firebase.database.OnCompleteListener
import com.diplom.tabletkaapp.firebase.database.OnReadCancelled
import com.diplom.tabletkaapp.models.AbstractModel
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.SettingsViewModel
import com.diplom.tabletkaapp.view_models.adapters.NotesAdapter
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase
import com.diplom.tabletkaapp.view_models.notes.NotesViewModel

class NotesFragment: Fragment() {
    var binding_: FragmentNoteListBinding? = null
    val binding get() = binding_!!
    val model: NotesViewModel =  NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding_ = FragmentNoteListBinding.inflate(inflater, container, false)
        initNoteList()
        initMenu()
        return binding.root
    }
    private fun initNoteList(){
        updateUI()
    }
    private fun initMenu(){
        binding.floatingActionButton.setOnClickListener {item ->
            val note = Note()
            note.id = "-1"
            findNavController(binding.root).navigate(
                NotesFragmentDirections.actionNavigationNotesToNotesRedactorFragment(note)
            )
        }
    }
    private fun updateUI(){
        model.loadFromFirebase(object : OnCompleteListener{
            override fun complete(list: MutableList<AbstractModel>) {
                FirebaseSettingsDatabase.readAll(SettingsViewModel()){
                    if(it.notesMode == 0){
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    } else {
                        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                    binding.recyclerView.adapter = NotesAdapter(list, it.notesMode){
                        updateUI()
                    }
                }
            }
        },
            object : OnReadCancelled{
                override fun cancel() {

                }

            })
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}