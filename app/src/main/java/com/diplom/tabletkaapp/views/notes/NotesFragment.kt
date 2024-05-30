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
import com.diplom.tabletkaapp.view_models.SettingsViewModel
import com.diplom.tabletkaapp.view_models.adapters.NotesAdapter
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
        initBackButton()
        initMenu()
        return binding.root
    }
    private fun initNoteList(){
        model.loadFromFirebase(object : OnCompleteListener{
            override fun complete(list: MutableList<AbstractModel>) {
                FirebaseSettingsDatabase.readAll(SettingsViewModel()){
                    if(it.notesMode == 0){
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    } else {
                        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                    binding.recyclerView.adapter = NotesAdapter(list, it.notesMode)
                }
            }
        },
        object : OnReadCancelled{
            override fun cancel() {

            }

        })
    }

    private fun initBackButton() {
        binding.materialToolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        binding.materialToolbar.setNavigationOnClickListener { v: View ->
            findNavController(binding.root).popBackStack()
        }
    }

    private fun initMenu(){
        binding.floatingActionButton.setOnClickListener {item ->
                findNavController(binding.root).navigate(
                    NotesFragmentDirections.actionNavigationNotesToNotesRedactorFragment()
                )
        }
    }
}