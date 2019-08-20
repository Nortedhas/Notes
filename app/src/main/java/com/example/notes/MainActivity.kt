package com.example.notes

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import com.example.notes.Adapter.AdapterNote
import com.example.notes.DataBase.DBHelper
import com.example.notes.Note.Note
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class MainActivity : AppCompatActivity() {

    private lateinit var myAdapter: AdapterNote
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private val items = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rv)

        rv.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        showAllNoteDB()

        sortDate(items)

        items.reverse()

        myAdapter = AdapterNote(items, object : AdapterNote.CallBack {

            override fun onItemClicked(item: Note) {

                var intent = Intent(applicationContext, NoteActivity::class.java)
                intent.putExtra(NoteActivity.ID_TEXT, item.id)
                intent.putExtra(NoteActivity.LIST_SIZE, items.size)
                intent.putExtra(NoteActivity.INTENT_STATUS, true)
                startActivity(intent)

            }
        })

        colorDrawableBackground = ColorDrawable(getColor(android.R.color.transparent))
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!

            val itemTouchHelperCallBack =
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                    ) {
                        val itemView = viewHolder.itemView
                        val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                        if (dX > 0) {
                            colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                            deleteIcon.setBounds(
                                itemView.left + iconMarginVertical,
                                itemView.top + iconMarginVertical,
                                itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                                itemView.bottom - iconMarginVertical
                            )
                        } else {
                            colorDrawableBackground.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            deleteIcon.setBounds(
                                itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                                itemView.top + iconMarginVertical,
                                itemView.right - iconMarginVertical,
                                itemView.bottom - iconMarginVertical
                            )
                            deleteIcon.level = 0
                        }

                        colorDrawableBackground.draw(c)

                        c.save()
                        if (dX > 0)
                            c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        else
                            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                        deleteIcon.draw(c)
                        c.restore()

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    }

                    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                        (myAdapter as AdapterNote).removeItem(applicationContext, p0.adapterPosition, p0)
                    }

                    override fun onMove(
                        p0: RecyclerView,
                        p1: RecyclerView.ViewHolder,
                        p2: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }
                }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
            itemTouchHelper.attachToRecyclerView(rv)

        myAdapter.notifyDataSetChanged()

        rv.adapter = myAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.add_note_item -> {

                var addIntent = Intent(applicationContext, NoteActivity::class.java)

                var size = items.size

                if(size!=0){
                    addIntent.putExtra(NoteActivity.LAST_ID,sortId(items))
                }

                addIntent.putExtra(NoteActivity.LIST_SIZE, size)

                startActivity(addIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAllNoteDB() {
        var idNote: Int
        var name: String
        var text: String
        var date: String
        val dbHandler = DBHelper(this, null)
        val cursor = dbHandler.getAllNote()

        cursor!!.moveToFirst()

        try {
            idNote = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID)))).toInt()
            name = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME))))
            text = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TEXT))))
            date = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE))))
            items.add(Note(idNote, name, text, date))
        }catch (e:Exception){
            Log.i("ERROR","Error")
        }
        while (cursor!!.moveToNext()) {
            idNote = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ID)))).toInt()
            name = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME))))
            text = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TEXT))))
            date = ((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE))))
            items.add(Note(idNote, name, text, date))
 }
        cursor!!.close()
        dbHandler.close()
    }

    private fun sortDate(items:ArrayList<Note>):ArrayList<Note>{

        var date=ArrayList<String>()

        for(i in 0 until items.size)
            date.add(items[i].date)

        date.sort()

        val sortedList=ArrayList<Note>()

        for(i in 0 until date.size) {
            for (j in 0 until items.size) {
                if (date[i] == items[j].date)
                    sortedList.add(items[j])
            }
        }

        items.clear()
        items.addAll(sortedList)
        return items

    }

    private fun sortId(items:ArrayList<Note>):Int {

        val listId=ArrayList<Int>()

        for(i in 0 until items.size)
            listId.add(items[i].id)

        listId.sort()

        return listId.last()
    }

}




