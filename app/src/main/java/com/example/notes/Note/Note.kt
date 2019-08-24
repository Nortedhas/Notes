package com.example.notes.Note

import java.io.Serializable


data class Note(
    val id: Int,
    var name:String,
    var text: String,
    var date: String):Serializable{
    constructor():this(0,"","","")
}
