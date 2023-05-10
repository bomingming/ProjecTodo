package com.example.projectodo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// SQLite 데이터베이스를 열기 위한 클래스
class MyDatabaseHelper(context: Context) : SQLiteOpenHelper (context, "opentutorials.sqlite", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}