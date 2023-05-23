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

    // project 테이블 insert 함수
    /*@Query("INSERT INTO project (project_title, start_day, end_day) VALUES (:project_title, :start_day, :end_day)")
    fun insertProjectQuery(project_title: String, start_day: String, end_day: String)*/

    // target 테이블 insert 함수(프로젝트 등록 시)
    /*@Query("INSERT INTO target(project_code_for, target_title, todo_count) VALUES " +
            "((SELECT project_code FROM project ORDER BY project_code DESC LIMIT 1), :target_title, :todo_count)")
    fun insertTargetQueryRegis(project_code_for: Int, target_title: String, todo_count: String)*/

    // todo 테이블 insert 함수(프로젝트 등록 시)
    /*@Query("INSERT INTO todo(target_code_for, todo_detail, end_check) VALUES " +
            "((SELECT project_code FROM project ORDER BY project_code DESC LIMIT 1), :todo_detail, :end_check)")
    fun insertTodoQueryRegis(target_code_for: Int, todo_detail: String, end_check: Int)*/


}