package com.example.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.notes.DataBase.DBHelper
import com.example.notes.Note.Note
import kotlinx.android.synthetic.main.item_note.*
import kotlinx.android.synthetic.main.note_activity.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity() {

    lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_activity)

        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy.MM.dd HH:mm:ss")

        var id = intent.getIntExtra(ID_TEXT, 0)

        var status = intent.getBooleanExtra(INTENT_STATUS, false)


        if (status) {
            showNoteDB(id)
            noteActivityName.setText(note.name)
            noteActivityText.setText(note.text)
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

                        var status = intent.getBooleanExtra(INTENT_STATUS, false)
                        var id = intent.getIntExtra(ID_TEXT, 0)
                        var lastId = intent.getIntExtra(LAST_ID, 0)
                        var backIntent = Intent(this, MainActivity::class.java)

                        if (status) {
                            var dbHandler = DBHelper(this, null)
                            note = Note(
                                id, noteActivityName.text.toString(),
                                noteActivityText.text.toString(),
                                noteActivityDate.text.toString()
                            )
                            dbHandler.updateNote(note)
                            dbHandler.close()

                            startActivity(backIntent)

                        } else {

                            var dbHandler = DBHelper(this, null)

                            note = Note(
                                lastId + 1, noteActivityName.text.toString(),
                                noteActivityText.text.toString(),
                                noteActivityDate.text.toString()
                            )

                            dbHandler.addNote(note)
                            dbHandler.close()

                            startActivity(backIntent)

                        }
                        return true
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showNoteDB(id: Int) {
        val idNote: Int
        val name: String
        val text: String
        val date: String
        val dbHandler = DBHelper(this, null)
        val cursor = dbHandler.getNote(id)

        cursor!!.moveToFirst()
        idNote = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID)))).toInt()
        name = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME))))
        text = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TEXT))))
        date = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE))))

        note = Note(idNote, name, text, date)

        cursor.close()
        dbHandler.close()
    }

    companion object{
        const val LIST_SIZE="list_size"
        const val INTENT_STATUS="STATUS_REQUEST"
        const val ID_TEXT="id_text"
        const val LAST_ID="last id"
      }
}

