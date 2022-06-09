package com.example.helpmemory

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyKeywordDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    var keywordFragment = KeywordFragment()
    var keywordData:ArrayList<MyKeywordData> =ArrayList()
    var addKeywordcheck = false

    companion object{

        val DB_NAME = "mydb.db"
        val DB_VERSION = 1
        val TABLE_NAME = "keyword"
        val KID = "id"
        val KEY = "keyword"
        val DES = "description"

    }
    fun getAllRecord(){
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql,null)
        transData(cursor)
        cursor.close()
        db.close()
    }

    fun transData(cursor: Cursor){
        var keyword = ""
        var description = ""
        var id = ""
        cursor.moveToFirst()
        val attrcount = cursor.columnCount
        do {
            for(i in 1 until attrcount step 3){
                keyword = cursor.getString(i)
                description = cursor.getString(i+1)
                id = cursor.getString(i-1)
                Log.d("test", keyword+description+id)
                keywordData.add(MyKeywordData(keyword, description, id))

            }
        }while (cursor.moveToNext())

    }

    fun insertKeyword(keywordData: MyKeywordData):Boolean{
        val values = ContentValues()
        values.put(KID, keywordData.id)
        values.put(KEY, keywordData.keyword)
        values.put(DES, keywordData.description)

        if(!addKeywordcheck){
            addKeywordcheck = true
        }

        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values) >0
        db.close()
        return flag
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val create_table = "create table if not exists $TABLE_NAME("+
                "$KID text primary key,"+
                "$KEY text,"+
                "$DES text );"


        db!!.execSQL(create_table)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersino: Int, newVersion: Int) {

        val drop_table = "drop table if exists  $TABLE_NAME;"

        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun deleteKeyword(id: String): Boolean{
        val strsql = "select * from $TABLE_NAME where $KID = '$id';"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql,null)
        val flag = cursor.count!=0

        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME, "$KID=?", arrayOf(id))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun updateKeyword(keywordData: MyKeywordData):Boolean{
        val id = keywordData.id
        val strsql = "select * from $TABLE_NAME where $KID = '$id';"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql,null)
        val flag = cursor.count!=0

        if(flag){
            cursor.moveToFirst()
            val values = ContentValues()
            values.put(KEY, keywordData.keyword)
            values.put(DES, keywordData.description)

            db.update(TABLE_NAME, values, "$KID=?", arrayOf(id.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }
}