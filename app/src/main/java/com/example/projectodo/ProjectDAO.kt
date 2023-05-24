package com.example.projectodo

import androidx.room.*

@Dao
interface ProjectDAO {
    @Insert
    fun insertProject(entity: ProjectEntity)

    @Insert
    fun insertTarget(entity: TargetEntity)

    @Insert
    fun insertTodo(entity: TodoEntity)

    @Update
    fun updateProject(entity: ProjectEntity)

    @Update
    fun updateTarget(entity: TargetEntity)

    @Update
    fun updateTodo(entity: TodoEntity)

    @Delete
    fun deleteProject(entity: ProjectEntity)

    @Delete
    fun deleteTarget(entity: TargetEntity)

    @Delete
    fun deleteTodo(entity: TodoEntity)

    @Query("SELECT * FROM Project")
    fun getAll(): List<ProjectEntity>

}