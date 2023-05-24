package com.example.projectodo

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ProjectEntity::class, TargetEntity::class, TodoEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDAO() : ProjectDAO

    companion object{
        var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase?{
            if(instance == null){
                synchronized(AppDatabase::class){
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "roomTestDB").build()
                }
            }
            return instance
        }
    }
}