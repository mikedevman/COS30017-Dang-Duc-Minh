package com.example.assignment3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

data class NoteDisplay(val name: String, val isTuned: Boolean)

class StringAdapter(
    private var notes: List<NoteDisplay>
) : RecyclerView.Adapter<StringAdapter.ViewHolder>() {

    var onItemClick: ((NoteDisplay, Int) -> Unit)? = null

    fun updateNotes(newNotes: List<NoteDisplay>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_string, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.stringNote.text = note.name

        if (note.isTuned) {
            holder.stringNote.setTextColor(0xFF00FF7F.toInt())
        } else {
            holder.stringNote.setTextColor(0xFFFFFFFF.toInt())
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(note, position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stringNote: TextView = view.findViewById(R.id.stringNote)
    }
}
