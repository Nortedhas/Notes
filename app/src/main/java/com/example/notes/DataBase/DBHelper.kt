package com.example.notes.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.notes.Note.Note

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context,DATABASE_NAME,factory,DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PRODUCT_TABLE=(
                "CREATE TABLE "+ TABLE_NAME
                + "( "
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME +" TEXT,"
                + COLUMN_TEXT + " TEXT,"
                + COLUMN_DATE+" TEXT)"
                )
        db!!.execSQL(CREATE_PRODUCT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXIST "+ TABLE_NAME)
        onCreate(db)
    }

    fun deleteNote(id: Int){
        val db=this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = $id",null)
    }

    fun updateNote(note:Note){
        val db=this.writableDatabase
        val cv=ContentValues()
        cv.put(COLUMN_ID,note.id)
        cv.put(COLUMN_NAME,note.name)
        cv.put(COLUMN_TEXT,note.text)
        cv.put(COLUMN_DATE,note.date)
        db.update(TABLE_NAME,cv,"_id=" + note.id, null)
    }

    fun addNote(note: Note){
        val values= ContentValues()
        values.put(COLUMN_ID, note.id)
        values.put(COLUMN_NAME, note.name)
        values.put(COLUMN_TEXT, note.text)
        values.put(COLUMN_DATE, note.date)
        val db=this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()

        Log.i("Add note",note.toString())
    }

    fun getAllNote() : Cursor?{
        val db=this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME",null)
    }

    fun getNote(id:Int) : Cursor?{
        val db=this.readableDatabase
        return db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${COLUMN_ID} = ${id}",null)
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "note.db"
        val TABLE_NAME = "note"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "_name"
        val COLUMN_TEXT = "_text"
        val COLUMN_DATE= "_date"
        val COLUMN_TIME= "_time"
    }
}