package com.example.simplenotes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplenotes.data.entity.Note
import com.example.simplenotes.databinding.ItemNotesBinding
import java.text.SimpleDateFormat

class NoteAdapter(private val mNotes:List<Note>,private val listener: OnNoteClickListener) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    interface OnNoteClickListener{
        fun onNoteClick(note: Note)
        fun onNoteLongClick(note: Note)
    }
    inner class ViewHolder(private val binding: ItemNotesBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                root.setOnClickListener{
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val note = mNotes[position]
                        listener.onNoteClick(note)
                    }
                }
                root.setOnLongClickListener{
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val note = mNotes[position]
                        listener.onNoteLongClick(note)
                    }
                    true
                }
            }
        }

        fun bind(note: Note){
            binding.apply {
                titleNote.text = note.title
                contentNote.text = note.content
                categoryNote.text = note.category
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                dateNote.text = formatter.format(note.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mNotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(mNotes[position]){
            holder.bind(this)

        }
    }
    fun updateNotes(newNotes: List<Note>) {
        notifyDataSetChanged() // Beritahu adapter bahwa data telah berubah
    }

}