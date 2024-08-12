package com.example.simplenotes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.simplenotes.data.entity.Note
import kotlinx.coroutines.flow.Flow
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Query( "Select * from notes")
    fun getAllNotes(): Flow<List<Note>>
    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)


}