package com.example.helpmemory

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.drm.DrmStore.DrmObjectType.CONTENT
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.datatype.DatatypeConstants.DATETIME

class ToDoDBHelper(val fragment: ToDoFragment) : SQLiteOpenHelper(fragment.requireContext(), DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_NAME = "tododb.db"
        val DB_VERSION = 1
        val TABLE_NAME = "to_do"
        val ID = "id"
        val TEXT = "text"
        val DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME(" +
                "$ID text primary key, " +
                "$TEXT text, " +
                "$DATE integer);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    @SuppressLint("Range")
    fun selectToDo(): MutableList<ToDo> {
        val list = mutableListOf<ToDo>()
        val selectAll = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor = db.rawQuery(selectAll,null)

        //반복문을 사용하여 list 에 데이터를 넘겨줍니다.
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(ID))
            val text = cursor.getString(cursor.getColumnIndex(TEXT))
            val date = cursor.getInt(cursor.getColumnIndex(DATE)).let {
                LocalDate.parse(it.toString(), DateTimeFormatter.ofPattern("yyyyMMdd"))
            }

            list.add(ToDo(id, text, date))
        }
        cursor.close()
        db.close()

        return list
    }

    fun insertToDo(toDo: ToDo) {
        val values = ContentValues()
        values.put(ID, toDo.id)
        values.put(TEXT, toDo.text)
        toDo.date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt().let {
            values.put(DATE, it)
        }
        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // select * from product where name = 'string';
    fun findToDo(date: Int): Boolean {
        val strsql = "select * from $TABLE_NAME where $DATE = '$date';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        cursor.close()
        db.close()
        return flag
    }

    fun deleteToDo(id: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id))
        db.close()
    }
}