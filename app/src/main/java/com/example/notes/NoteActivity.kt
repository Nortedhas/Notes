package com.example.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.notes.Note.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.note_activity.*
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    val TAG="TAG"

    var note: Note?=Note(0,"","","")
    val user=FirebaseAuth.getInstance().currentUser
    val ref= FirebaseDatabase.getInstance().getReference(user!!.uid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_activity)

        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy.MM.dd HH:mm:ss")

        val status = intent.getBooleanExtra(INTENT_STATUS, false)

        if (status) {

            note=intent.extras.getSerializable(NOTE) as Note?

            noteActivityName.setText(note!!.name)
            noteActivityText.setText(note!!.text)
        }

        noteActivityDate.text = dateInString

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.add_item_menu -> {

                    if (noteActivityName.length() == 0 || noteActivityText.length() == 0) {
                        Snackbar.make(getWindow().currentFocus, "Please, enter wish!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.WHITE)
                            .show()
                    } else {

                        if (noteActivityName.text.toString().trim().equals("Spice")) {
                            window.setBackgroundDrawable(getDrawable(R.drawable.rikroll))
                        } else {

                        val status = intent.getBooleanExtra(INTENT_STATUS, false)
                        val id = intent.getIntExtra(ID_TEXT, 0)
                        val lastId = intent.getIntExtra(LAST_ID, 0)
                        val backIntent = Intent(this, MainActivity::class.java)

                        if (status) {
                            note = Note(
                                id, noteActivityName.text.toString(),
                                noteActivityText.text.toString(),
                                noteActivityDate.text.toString()
                            )
                            saveNote(note!!)

                            startActivity(backIntent)

                        } else {


                            note = Note(
                                lastId + 1, noteActivityName.text.toString(),
                                noteActivityText.text.toString(),
                                noteActivityDate.text.toString()
                            )

                            saveNote(note!!)

                            startActivity(backIntent)

                        }
                        return true
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote(note:Note){
        ref.child(note.id.toString()).setValue(note)
    }

    companion object{
        const val INTENT_STATUS="STATUS_REQUEST"
        const val ID_TEXT="id_text"
        const val LAST_ID="last id"
        const val NOTE="note"
      }
}

