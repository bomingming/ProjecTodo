package com.example.projectodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log

// SQLite 데이터베이스를 열기 위한 클래스
class MyDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, 1){
    companion object{
        const val DB_NAME = "Projectodo"
        const val TB_NAME = "project"
        const val COL1_CODE = "project_code"
        const val COL2_TITLE = "project_title"
        const val COL3_START = "start_day"
        const val COL4_END = "end_day"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $TB_NAME (project_code INTEGER PRIMARY KEY AUTOINCREMENT, project_title TEXT, start_day TEXT, end_day TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TB_NAME")
        onCreate(db)
    }

    fun insertData(db: SQLiteDatabase?): SQLiteDatabase? {
        //val db = this.writableDatabase
        //val contentValues = ContentValues()
        /*contentValues.put(COL2_TITLE, title)
        contentValues.put(COL3_START, start)
        contentValues.put(COL4_END, end)*/
        db?.execSQL("insert into $TB_NAME ($COL2_TITLE, $COL3_START, $COL4_END) values (?, ?, ?)",
        arrayOf<String>("테스트 프로젝트", "2023년 3월 1일", "2023년 5월 1일")
        )
        //val result = db.insert(TB_NAME, null, contentValues)
        //return result != -1L
        Log.e("insertData()의 반환값", db.toString())
        return db
    }

    fun getAllData(): Cursor{
        val db = this.writableDatabase
        return db.rawQuery("select * from $TB_NAME", null)
    }

}