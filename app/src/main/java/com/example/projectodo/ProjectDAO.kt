package com.example.projectodo

import androidx.room.*

@Dao
interface ProjectDAO {
    @Insert
    fun insert(entity: ProjectEntity)

    @Insert
    fun insert(entity: TargetEntity)

    @Insert
    fun insert(entity: TodoEntity)

    @Update
    fun update(entity: ProjectEntity)

    @Update
    fun update(entity: TargetEntity)

    @Update
    fun update(entity: TodoEntity)

    @Delete
    fun delete(entity: ProjectEntity)

    @Delete
    fun delete(entity: TargetEntity)

    @Delete
    fun delete(entity: TodoEntity)

    @Query()
}