package com.diplom.tabletkaapp.views.notes.holders

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diplom.tabletkaapp.databinding.ItemNoteLinearBinding
import com.diplom.tabletkaapp.models.data_models.Note
import com.diplom.tabletkaapp.view_models.firebase.database.FirebaseNotesDatabase
import com.diplom.tabletkaapp.views.notes.NotesFragmentDirections
/**
 * Представление заметки в виде списка
 */
class NoteHolderLinear(
    var binding: ItemNoteLinearBinding
): RecyclerView.ViewHolder(binding.root) {
    /**
     * Привязка текста к представлению
     * Если заголовка нету, то описание делится на 2 части и 1  устнавливается в заголовок, другая в описание
     *
     * Инициализация закрепления заметки
     */
    fun bind(note: Note, onUpdateUI: () -> Unit){
        binding.title.text = note.name
        binding.text.text  = note.describe
        binding.root.setOnClickListener{
            findNavController(binding.root).navigate(
                NotesFragmentDirections.actionNavigationNotesToNotesRedactorFragment(note)
            )
        }
        if (note.name.isNotEmpty()) {
            binding.title.text = note.name
        } else if (note.name.isEmpty() && note.describe.isNotEmpty()) {
            val length: Int = note.describe.length
            val halfLength = length / 2
            note.name = note.describe.substring(0, halfLength)
            binding.title.text = note.describe.substring(0, halfLength)
            note.describe = note.describe.substring(halfLength, length)
        }
        binding.root.setOnLongClickListener { v ->
            val builder =
                AlertDialog.Builder(binding.root.context)
            builder.setMessage("Что вы хотите сделать?")
                .setNeutralButton(
                    "Закрыть"
                )
                { _, _ -> }
                .setPositiveButton(getWishString(note)
                ) { _, _ ->
                    note.wish = !note.wish
                    binding.linkImage.visibility = when (note.wish) {
                        false -> View.GONE
                        true -> View.VISIBLE
                    }
                    FirebaseNotesDatabase.add(note)
                }
                .setNegativeButton("Удалить"
                ) { _, _ ->
                    FirebaseNotesDatabase.delete(note)
                    onUpdateUI.invoke()
                }
            val dialog = builder.create()
            dialog.show()
            true
        }
        binding.linkImage.visibility = when(note.wish){
            false -> View.GONE
            true -> View.VISIBLE
        }
    }

    /**
     * Метод для получения строки о нынешнем состоянии заметки
     */
    private fun getWishString(note: Note): String{
        return when(note.wish){
            false -> "Закрепить"
            true -> "Открепить"
        }
    }
}