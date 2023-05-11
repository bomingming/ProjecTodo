package com.example.projectodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// SQLite 데이터베이스를 열기 위한 클래스
class MyDatabaseHelper(context: Context) : SQLiteOpenHelper (context, "projectodo ", null, 1){
    val TABLE_NAME = "project"
    val COL1_PJCODE = "project_code"
    val COL2_TITLE = "project_title"
    val COL3_START = "start_day"
    val COL4_END = "end_day"

    // 예제 복사
    @Volatile
    private var instance:MyDatabaseHelper? = null

    fun getInstance(context: Context) =
        instance ?: synchronized(MyDatabaseHelper::class.java){
            instance ?: MyDatabaseHelper(context).also{
                instance=it
            }
        }

    override fun onCreate(db: SQLiteDatabase) {
        val createProjectTB = "CREATE TABLE $TABLE_NAME ($COL1_PJCODE INTEGER PRIMARY KEY AUTOINCREMENT, $COL2_TITLE TEXT, $COL3_START TEXT, $COL4_END TEXT)"
        db?.execSQL(createProjectTB)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
    fun insertData(title: String, start: String, end: String){
        val db: SQLiteDatabase = this.writableDatabase
        val contentValue: ContentValues = ContentValues().apply {
            put(COL2_TITLE, title)
            put(COL3_START, start)
            put(COL4_END, end)
        }
        db.insert(TABLE_NAME, null, contentValue)
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