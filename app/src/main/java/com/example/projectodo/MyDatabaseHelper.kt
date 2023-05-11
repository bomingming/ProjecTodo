package com.example.projectodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File

// SQLite 데이터베이스를 열기 위한 클래스
class MyDatabaseHelper(private val context: Context) : SQLiteOpenHelper (context, "projectodo.sqlite3", null, 1){

    private val DB_PATH = context.getExternalFilesDir(null)!!.absolutePath
    private val DB_NAME = "projectodo.sqlite3"
    private val TABLE_NAME = "project"
    private val COL1_PJCODE = "project_code"
    private val COL2_TITLE = "project_title"
    private val COL3_START = "start_day"
    private val COL4_END = "end_day"

    private fun createDatabase(){
        val databasePath = DB_PATH + File.separator + DB_NAME
        if(!File(databasePath).exists()){
            val db = SQLiteDatabase.openOrCreateDatabase(databasePath, null)
            val createProjectTB = "CREATE TABLE $TABLE_NAME ($COL1_PJCODE INTEGER PRIMARY KEY AUTOINCREMENT, $COL2_TITLE TEXT, $COL3_START TEXT, $COL4_END TEXT)"
            db.execSQL(createProjectTB)
            db.close()
        }
    }
    companion object {
        @Volatile private var instance: MyDatabaseHelper? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this){
                instance ?: MyDatabaseHelper(context).also { instance = it }
            }
    }

    fun insertData(title: String, start: String, end: String): Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL2_TITLE, title)
        contentValues.put(COL3_START, start)
        contentValues.put(COL4_END, end)
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createProjectTB = "CREATE TABLE $TABLE_NAME ($COL1_PJCODE INTEGER PRIMARY KEY AUTOINCREMENT, $COL2_TITLE TEXT, $COL3_START TEXT, $COL4_END TEXT)"
        db.execSQL(createProjectTB)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllData(): String{
        var result = "No data in DB"

        val db:SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)


        try{
            if(cursor.count != 0){
                val stringBuffer = StringBuffer()
                while(cursor.moveToNext()){
                    stringBuffer.append("PJ_CODE : " + cursor.getInt(0).toString()+"\n")
                    stringBuffer.append("TITLE : " + cursor.getString(1)+"\n")
                    stringBuffer.append("START : " + cursor.getString(2)+"\n")
                    stringBuffer.append("END : " + cursor.getString(3)+"\n")
                }
                result = stringBuffer.toString()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            if(cursor != null && !cursor.isClosed){
                cursor.close()
            }
        }
        return result
    }
}