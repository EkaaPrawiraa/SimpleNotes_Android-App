package com.example.simplenotes.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val title: String?,
    val content: String?,
    val category: String?,
    val date_created : Long,
    val date: Long
):Parcelable
