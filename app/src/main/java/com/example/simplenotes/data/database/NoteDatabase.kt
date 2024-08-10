package com.example.simplenotes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplenotes.data.dao.NoteDao
import com.example.simplenotes.data.entity.Note

@Database(entities = arrayOf(Note::class), version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}