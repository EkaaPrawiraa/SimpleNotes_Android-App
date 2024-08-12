package com.example.simplenotes.ui

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplenotes.R
import com.example.simplenotes.adapter.NoteAdapter
import com.example.simplenotes.data.entity.Note
import com.example.simplenotes.databinding.FragmentNotesBinding
import com.example.simplenotes.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener
import androidx.preference.PreferenceManager

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.fragment_notes), NoteAdapter.OnNoteClickListener {
    private val viewModel by viewModels<NoteViewModel>()
    private lateinit var binding: FragmentNotesBinding
    private var useQuickSort: Boolean = true
    private var useKMP: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNotesBinding.bind(requireView())


        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        useQuickSort = sharedPreferences.getBoolean("filterAlgorithm", true)
        useKMP = sharedPreferences.getBoolean("searchAlgorithm", true)

        binding.apply {
            recyclerViewNotes.layoutManager = GridLayoutManager(context, 2)
            recyclerViewNotes.setHasFixedSize(true)

            addBtn.setOnClickListener {
                val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(null)
                findNavController().navigate(action)
            }

            sortButton.setOnClickListener {
                showSortOptionsDialog()
            }

            searchInput.addTextChangedListener { text ->
                val query = text.toString()
                updateNotesList(query, categoryFilterInput.text.toString())
            }

            categoryFilterInput.addTextChangedListener { text ->
                val query = text.toString()
                updateNotesList(searchInput.text.toString(), query)
            }

            settingBtn.setOnClickListener {
                findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToSettingsFragment())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.notes.collect { noteList ->
                    val sortedNotes = viewModel.sortNotes(
                        noteList,
                        viewModel.getSelectedSortOption(),
                        useQuickSort
                    )
                    val adapter = NoteAdapter(sortedNotes, this@NoteFragment)
                    recyclerViewNotes.adapter = adapter
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.notesEvent.collect { event ->
                    if (event is NoteViewModel.NotesEvent.ShowUndoSnackBar) {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.insertNote(event.note)
                            }.show()
                    }
                }
            }
            Log.d("algos", useQuickSort.toString())
            Log.d("algof", useKMP.toString())

        }
    }

    private fun updateNotesList(searchQuery: String, categoryQuery: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notes.collect { noteList ->
                val filteredNotes = if (categoryQuery.isNotEmpty()) {
                    viewModel.filterNotesByCategory(noteList, categoryQuery, useKMP)
                } else {
                    noteList
                }
                val searchedNotes = if (searchQuery.isNotEmpty()) {
                    viewModel.searchNotes(filteredNotes, searchQuery, useKMP)
                } else {
                    filteredNotes
                }
                val sortedNotes = viewModel.sortNotes(
                    searchedNotes,
                    viewModel.getSelectedSortOption(),
                    useQuickSort
                )
                val adapter = NoteAdapter(sortedNotes, this@NoteFragment)
                binding.recyclerViewNotes.adapter = adapter
            }
        }
    }

    private fun showSortOptionsDialog() {
        val sortOptions = arrayOf(
            "Title (A-Z)", "Title (Z-A)",
            "Created At (Newest)", "Created At (Oldest)",
            "Updated At (Newest)", "Updated At (Oldest)"
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Sort by")
            .setItems(sortOptions) { _, which ->
                val selectedOption = when (which) {
                    0 -> NoteViewModel.SortOption.TITLE_ASC
                    1 -> NoteViewModel.SortOption.TITLE_DESC
                    2 -> NoteViewModel.SortOption.CREATED_AT_ASC
                    3 -> NoteViewModel.SortOption.CREATED_AT_DESC
                    4 -> NoteViewModel.SortOption.UPDATED_AT_ASC
                    5 -> NoteViewModel.SortOption.UPDATED_AT_DESC
                    else -> NoteViewModel.SortOption.TITLE_ASC
                }
                viewModel.setSelectedSortOption(selectedOption)
                refreshSortedNotes()
            }
            .show()
    }

    private fun refreshSortedNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notes.collect { noteList ->
                val sortedNotes =
                    viewModel.sortNotes(noteList, viewModel.getSelectedSortOption(), useQuickSort)
                val adapter = NoteAdapter(sortedNotes, this@NoteFragment)
                binding.recyclerViewNotes.adapter = adapter
            }
        }
    }

    override fun onNoteClick(note: Note) {
        val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(note)
        findNavController().navigate(action)
    }

    override fun onNoteLongClick(note: Note) {
        viewModel.deleteNote(note)
    }

}
