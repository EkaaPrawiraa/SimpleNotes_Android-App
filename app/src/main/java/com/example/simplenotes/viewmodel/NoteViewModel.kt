package com.example.simplenotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenotes.data.dao.NoteDao
import com.example.simplenotes.data.entity.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteDao: NoteDao):ViewModel() {

    val notes = noteDao.getAllNotes()
    val notesChannel = Channel<NotesEvent>()
    val notesEvent = notesChannel.receiveAsFlow()
    private var selectedSortOption: SortOption = SortOption.TITLE_ASC

    fun insertNote(note: Note)  = viewModelScope.launch {
        noteDao.insertNote(note)
        notesChannel.send(NotesEvent.NavigateToNoteFragment)
    }

    fun updateNote(note: Note)  = viewModelScope.launch {
        noteDao.updateNote(note)
        notesChannel.send(NotesEvent.NavigateToNoteFragment)
    }
    fun deleteNote(note: Note)  = viewModelScope.launch {
        noteDao.deleteNote(note)
        notesChannel.send(NotesEvent.ShowUndoSnackBar("Note Deleted Successfully",note))
    }
    fun sortNotes(notes: List<Note>, sortBy: SortOption, useQuickSort: Boolean): List<Note> {
        return when (sortBy) {
            SortOption.TITLE_ASC -> if (useQuickSort) quickSort(notes) { a, b -> compareTitles(a.title.toString(), b.title.toString()) } else mergeSort(notes) { a, b -> compareTitles(a.title.toString(), b.title.toString()) }
            SortOption.TITLE_DESC -> if (useQuickSort) quickSort(notes) { a, b -> compareTitles(b.title.toString(), a.title.toString()) } else mergeSort(notes) { a, b -> compareTitles(b.title.toString(), a.title.toString()) }
            SortOption.CREATED_AT_ASC -> if (useQuickSort) quickSort(notes) { a, b -> a.date_created.compareTo(b.date_created) } else mergeSort(notes) { a, b -> a.date_created.compareTo(b.date_created) }
            SortOption.CREATED_AT_DESC -> if (useQuickSort) quickSort(notes) { a, b -> b.date_created.compareTo(a.date_created) } else mergeSort(notes) { a, b -> b.date_created.compareTo(a.date_created) }
            SortOption.UPDATED_AT_ASC -> if (useQuickSort) quickSort(notes) { a, b -> a.date.compareTo(b.date) } else mergeSort(notes) { a, b -> a.date.compareTo(b.date) }
            SortOption.UPDATED_AT_DESC -> if (useQuickSort) quickSort(notes) { a, b -> b.date.compareTo(a.date) } else mergeSort(notes) { a, b -> b.date.compareTo(a.date) }
        }
    }

    fun compareTitles(title1: String, title2: String): Int {
        val lexicographicalComparison = title1.compareTo(title2)
        return if (lexicographicalComparison == 0) {
            title1.length.compareTo(title2.length)
        } else {
            lexicographicalComparison
        }
    }

    private fun quickSort(list: List<Note>, compare: (Note, Note) -> Int): List<Note> {
        if (list.size <= 1) return list
        val pivot = list[list.size / 2]
        val equal = list.filter { compare(it, pivot) == 0 }
        val less = list.filter { compare(it, pivot) < 0 }
        val greater = list.filter { compare(it, pivot) > 0 }
        return quickSort(less, compare) + equal + quickSort(greater, compare)
    }

    private fun mergeSort(list: List<Note>, compare: (Note, Note) -> Int): List<Note> {
        if (list.size <= 1) return list
        val middle = list.size / 2
        val left = mergeSort(list.subList(0, middle), compare)
        val right = mergeSort(list.subList(middle, list.size), compare)
        return merge(left, right, compare)
    }

    private fun merge(left: List<Note>, right: List<Note>, compare: (Note, Note) -> Int): List<Note> {
        var i = 0
        var j = 0
        val merged = mutableListOf<Note>()
        while (i < left.size && j < right.size) {
            if (compare(left[i], right[j]) <= 0) {
                merged.add(left[i])
                i++
            } else {
                merged.add(right[j])
                j++
            }
        }
        merged.addAll(left.subList(i, left.size))
        merged.addAll(right.subList(j, right.size))
        return merged
    }



    sealed class NotesEvent {
        data class ShowUndoSnackBar(val msg: String, val note: Note) : NotesEvent()
        object NavigateToNoteFragment: NotesEvent()
    }
    enum class SortOption {
        TITLE_ASC,
        TITLE_DESC,
        CREATED_AT_ASC,
        CREATED_AT_DESC,
        UPDATED_AT_ASC,
        UPDATED_AT_DESC
    }
    fun setSelectedSortOption(sortOption: SortOption) {
        selectedSortOption = sortOption
    }

    fun getSelectedSortOption(): SortOption {
        return selectedSortOption
    }
    fun filterNotesByCategory(notes: List<Note>, query: String, useKMP: Boolean = true): List<Note> {
        return notes.filter { note ->
            if (useKMP) {
                kmpSearch(note.category.toString(), query)
            } else {
                bmSearch(note.category.toString(), query)
            }
        }
    }

    fun kmpSearch(text: String, pattern: String): Boolean {
        val prefixTable = buildPrefixTable(pattern)
        var i = 0
        var j = 0

        while (i < text.length) {
            if (text[i].equals(pattern[j], ignoreCase = true)) {
                i++
                j++
                if (j == pattern.length) {
                    return true
                }
            } else {
                if (j != 0) {
                    j = prefixTable[j - 1]
                } else {
                    i++
                }
            }
        }
        return false
    }

    private fun buildPrefixTable(pattern: String): IntArray {
        val prefixTable = IntArray(pattern.length)
        var j = 0
        var i = 1

        while (i < pattern.length) {
            if (pattern[i].equals(pattern[j], ignoreCase = true)) {
                j++
                prefixTable[i] = j
                i++
            } else {
                if (j != 0) {
                    j = prefixTable[j - 1]
                } else {
                    prefixTable[i] = 0
                    i++
                }
            }
        }
        return prefixTable
    }

    fun bmSearch(text: String, pattern: String): Boolean {
        if (pattern.isEmpty()) return true
        if (pattern.length > text.length) return false

        val badCharTable = buildBadCharTable(pattern)

        var offset = 0
        while (offset <= text.length - pattern.length) {
            var j = pattern.length - 1

            while (j >= 0 && pattern[j].equals(text[offset + j], ignoreCase = true)) {
                j--
            }

            if (j < 0) {
                return true
            } else {
                offset += maxOf(1, j - badCharTable[text[offset + j].toInt()])
            }
        }
        return false
    }

    private fun buildBadCharTable(pattern: String): IntArray {
        val table = IntArray(256) { pattern.length }

        for (i in 0 until pattern.length - 1) {
            table[pattern[i].toInt()] = pattern.length - 1 - i
        }
        return table
    }
    fun searchNotes(notes: List<Note>, query: String, useKMP: Boolean = true): List<Note> {
        return notes.filter { note ->
            val titleMatch = if (useKMP) kmpSearch(note.title.toString(), query) else bmSearch(note.title.toString(), query)
            val noteMatch = if (useKMP) kmpSearch(note.content.toString(), query) else bmSearch(note.content.toString(), query)

            when {
                titleMatch && noteMatch -> true
                titleMatch -> true
                noteMatch -> true
                else -> false
            }
        }
    }





}