package com.example.projectodo

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(ProjectEntity::class, TargetEntity::class, TodoEntity::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDAO() : ProjectDAO

    companion object{
        var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase?{
            return instance ?: synchronized(this){
                val migration2 = object : Migration(1, 2){
                    override fun migrate(database: SupportSQLiteDatabase) {
                    }
                }
                instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "roomTestDB").fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}