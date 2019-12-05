package com.moeiny.reza.kotlin_divar.fragment

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

val dbName = "businessdays"
val tblName = "hollyday"
val colId = "id"
val colDate = "date"
val colDayName = "day_name"
val colDescription = "description"
val colDayofYear = "day_of_year"
val colDayofWeek = "day_of_week"
val dbVersion = 1
val createDatabaseQuery = "CREATE TABLE IF NOT EXISTS " + tblName + " (" +
        colId + " INTEGER PRIMARY KEY, " + colDate + " TEXT, "+ colDayName + " TEXT, "+ colDescription + " TEXT, "+ colDayofYear + " INTEGER, "+
        colDayofWeek + " INTEGER);"

class MyDatabase(context: Context) : SQLiteOpenHelper(context, dbName, null, dbVersion) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(createDatabaseQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + tblName)
    }

    fun insertData(values: ContentValues): Long {
        var db = this.writableDatabase
        val id = db!!.insert(tblName, "", values)
        return id
    }

    fun getAllData(): Cursor {
        var db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM " + tblName, null)
        } catch (e: Exception) {
            Log.i("kotlin", e.toString())
        }

        return cursor!!

    }
    fun getUniqData(date:String): Cursor {
        var db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM " + tblName+" where "+colDate+" = "+date,null)
        } catch (e: Exception) {
            Log.i("kotlin", e.toString())
        }

        return cursor!!

    }

    fun delete(id: Int):Int {
        var db = this.writableDatabase
        var selectionArgs = arrayOf(id.toString())
        var id = db.delete(tblName, "id=?", selectionArgs)
        return  id
    }

    fun updateRow(id:Int,values: ContentValues){
        var db=this.writableDatabase
        var selectionArgs= arrayOf(id.toString())
        db.update(tblName,values,"id=?",selectionArgs)
    }

}