package com.example.notes

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.notes.Adapter.AdapterNote
import com.example.notes.Authentication.AuthenticationActivity
import com.example.notes.Note.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var myAdapter: AdapterNote
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private var items = ArrayList<Note>()
    private var user= FirebaseAuth.getInstance().currentUser
    private val ref= FirebaseDatabase.getInstance().getReference(user!!.uid)
    private var firebaseUser=FirebaseAuth.getInstance()
    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rv)

        dbRef=ref.ref

        rv.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        showAllNoteDB()

        updateList()

        sortDate(items)

        items.reverse()



        myAdapter = AdapterNote(items, object : AdapterNote.CallBack {

            override fun onItemClicked(item: Note) {

                var intent = Intent(applicationContext, NoteActivity::class.java)
                intent.putExtra(NoteActivity.ID_TEXT, item.id)
                intent.putExtra(NoteActivity.NOTE,item)
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

        rv.adapter = myAdapter
        myAdapter.notifyDataSetChanged()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            R.id.add_note_item -> {

                var addIntent = Intent(applicationContext, NoteActivity::class.java)

                if(!items.isEmpty()){
                    addIntent.putExtra(NoteActivity.LAST_ID,sortId(items))
                }

                startActivity(addIntent)
                return true
            }

            R.id.log_out_menu->{
                firebaseUser.signOut()
                startActivity(Intent(applicationContext,AuthenticationActivity::class.java))

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAllNoteDB() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0!!.exists()) {
                    for (i in p0.children) {
                        val noteItem = i.getValue(Note::class.java)
                        items.add(noteItem!!)
                    }
                }
            }
        })

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

    private fun updateList(){
        GlobalScope.launch {
            delay(1000)
                dbRef.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                       // showAllNoteDB()
                        myAdapter.notifyDataSetChanged()

                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                        items.clear()
                        myAdapter.notifyDataSetChanged()
                    }

                })
            }
    }

}




