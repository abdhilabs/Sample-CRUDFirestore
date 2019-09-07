package com.abdhilabs.crudfirestore.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abdhilabs.crudfirestore.NoteActivity
import com.abdhilabs.crudfirestore.R
import com.abdhilabs.crudfirestore.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(private val noteList: MutableList<Note>, private val context: Context, private val firestoreDB:FirebaseFirestore)
    : RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note,parent,false))
    }

    override fun getItemCount(): Int = noteList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList[position]
        holder.title.text = note.title
        holder.content.text = note.content

        holder.edit.setOnClickListener{updateNote(note)}
        holder.delete.setOnClickListener { deleteNote(note.id!!,position) }
    }

    private fun updateNote(note: Note) {
        val intent = Intent(context,NoteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateNoteId", note.id)
        intent.putExtra("UpdateNoteTitle", note.title)
        intent.putExtra("UpdateNoteContent", note.content)
        context.startActivity(intent)
    }

    private fun deleteNote(id: String, position: Int) {
        firestoreDB.collection("notes")
            .document(id)
            .delete()
            .addOnCompleteListener {
                noteList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position,noteList.size)
                Toast.makeText(context,"Note has been deleted!",Toast.LENGTH_SHORT).show()
            }
    }


    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        internal var title: TextView = view.findViewById(R.id.tvTitle)
        internal var content: TextView = view.findViewById(R.id.tvContent)
        internal var edit: ImageView = view.findViewById(R.id.ivEdit)
        internal var delete: ImageView = view.findViewById(R.id.ivDelete)

    }
}