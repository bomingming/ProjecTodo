package com.example.projectodo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProjectDAO {
    @Insert
    fun insert(entity: ProjectEntity)

    @Insert
    fun insert(entity: TargetEntity)

    @Insert
    fun insert(entity: TodoEntity)



    @Query()
}