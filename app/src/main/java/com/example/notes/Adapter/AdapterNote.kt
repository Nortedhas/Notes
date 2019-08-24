package com.example.notes.Adapter

import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.notes.Note.Note
import com.example.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AdapterNote(var items:ArrayList<Note>,val callback: CallBack): RecyclerView.Adapter<AdapterNote.MyViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)=MyViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_note, p0, false))

    override fun getItemCount():Int=items.size

    private var removedPosition: Int = 0
    private var removedItem: Note = Note(0,"","","")
    private val user= FirebaseAuth.getInstance().currentUser
    private val ref= FirebaseDatabase.getInstance().getReference(user!!.uid)


    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        p0.bind(items[p1])
    }


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            val name = itemView.findViewById<TextView>(R.id.noteName)
            val description = itemView.findViewById<TextView>(R.id.noteDescription)
            val date = itemView.findViewById<TextView>(R.id.noteDate)

        fun bind(item:Note){
            name.text=item.name
            description.text=item.text
            date.text=item.date
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }


    }
    interface CallBack {
        fun onItemClicked(item: Note)
    }

    fun removeItem(contex:Context, position:Int, viewHolder:RecyclerView.ViewHolder) {

        removedItem = items[position]
        removedPosition = position

        notifyItemRemoved(removedPosition)
        deleteNote(removedItem)

        Snackbar.make(viewHolder.itemView, "${removedItem.name} removed", Snackbar.LENGTH_LONG).setAction("UNDO") {

            items.clear()
            notifyDataSetChanged()

            saveNote(removedItem)

        }.setActionTextColor(Color.WHITE)
            .show()

    }
    private fun saveNote(note:Note){
        ref.child(note.id.toString()).setValue(note)
    }
    private fun deleteNote(note:Note){
        ref.child(note.id.toString()).removeValue()
    }
}





