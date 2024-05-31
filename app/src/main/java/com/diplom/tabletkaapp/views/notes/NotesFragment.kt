package com.diplom.tabletkaapp.views.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
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
import com.diplom.tabletkaapp.view_models.notes.NotesViewModel
import java.util.Locale
import java.util.function.Predicate
import java.util.stream.Collectors

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchView()
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
                FirebaseSettingsDatabase.readAll(SettingsViewModel()){ it ->
                    if(it.notesMode == 0){
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    } else {
                        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                    list.sortBy{note -> !note.wish}
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

    private fun initSearchView(){
        val searchView: SearchView? = binding.materialToolbar.findViewById(R.id.app_bar_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isEmpty()){
                    (binding.recyclerView.adapter as NotesAdapter).resetList(model.list)
                }
                val regex = Regex(newText)
                (binding.recyclerView.adapter as NotesAdapter).resetList(model.list.filter{ note: AbstractModel ->
                    regex.findAll(
                        note.name.lowercase(),
                        0
                    ).iterator().hasNext()
                } as MutableList<AbstractModel>)
                return false
            }
        })
    }
}