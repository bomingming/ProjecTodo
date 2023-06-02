package com.example.projectodo

import android.database.Cursor
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
    fun getAllProject(): List<ProjectEntity>

    // 프로젝트 기본 키로 프로젝트 정보 가져오기
    @Query("SELECT * FROM project WHERE project_code = :projectCode")
    fun getProjectByCode(projectCode: Int): ProjectEntity

    // 프로젝트 삭제
    /*@Query("DELETE FROM project WHERE project_code = :projectCode")
    fun deleteProject(projectCode: Int): ProjectEntity*/

}