package com.diplom.tabletkaapp.views.notes.holders

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemNoteGridBinding
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase
import com.diplom.tabletkaapp.views.notes.NotesFragmentDirections

class NoteHolderGrid(
    var binding: ItemNoteGridBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note, onUpdateUI: () -> Unit){
        binding.title.text = note.name
        binding.text.text  = note.describe
        binding.root.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(
                NotesFragmentDirections.actionNavigationNotesToNotesRedactorFragment(note)
            )
        }
        if (!note.name.isEmpty()) {
            binding.title.setText(note.name)
        } else if (note.name.isEmpty() && !note.describe.isEmpty()) {
            val length: Int = note.describe.length
            val halfLength = length / 2
            binding.title.setText(note.describe.substring(0, halfLength))
            note.describe = note.describe.substring(halfLength, length)
        }
        binding.root.setOnLongClickListener { v ->
            val builder =
                AlertDialog.Builder(binding.root.context)
            builder.setMessage("Что вы хотите сделать?")
                .setNeutralButton(
                    "Закрыть"
                )
                { dialog: DialogInterface?, which: Int -> }
                .setPositiveButton(getWishString(note)
                ) { dialog: DialogInterface?, id: Int ->
                    note.wish = !note.wish
                    binding.linkImage.visibility = when (note.wish) {
                        false -> View.GONE
                        true -> View.VISIBLE
                    }
                    FirebaseNotesDatabase.add(note) {
                        onUpdateUI.invoke()
                    }
                }
                .setNegativeButton("Удалить"
                ) { dialog: DialogInterface?, id: Int ->
                    FirebaseNotesDatabase.delete(note) {
                        onUpdateUI.invoke()
                    }
                }
            val dialog = builder.create()
            dialog.show()
            true
        }
    }
    private fun getWishString(note: Note): String{
        return when(note.wish){
            false -> "Закрепить"
            true -> "Открепить"
        }
    }
}