package com.example.projectodo

import android.database.Cursor
import androidx.room.*

@Dao
interface ProjectDAO {
    @Insert
    fun insertProject(entity: ProjectEntity) : Long

    @Insert
    fun insertTarget(entity: TargetEntity): Long

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

    // 모든 프로젝트
    @Query("SELECT * FROM Project")
    fun getAllProject(): List<ProjectEntity>

    @Query("SELECT * FROM Target")
    fun getAllTarget(): List<TargetEntity>

    // 프로젝트 기본 키로 프로젝트 정보 가져오기
    @Query("SELECT * FROM project WHERE project_code = :projectCode")
    fun getProjectByCode(projectCode: Int): ProjectEntity

    // 해당 프로젝트의 목표 정보 가져오기
     @Query("SELECT * FROM target WHERE project_code_for = :projectCode")
     fun getTargetByCode(projectCode: Int): List<TargetEntity>

     // 해당 목표의 일정 정보 가져오기
     @Query("SELECT * FROM todo WHERE target_code_for = :targetCode")
     fun getTodoByCode(targetCode: Int) : List<TodoEntity>

    // 프로젝트 삭제
    @Query("DELETE FROM project WHERE project_code = :projectCode")
    fun deleteProject(projectCode: Int)

    // 프로젝트 코드 기준 목표 전부 삭제
    @Query("DELETE FROM target WHERE project_code_for = :projectCode")
    fun deleteTarget(projectCode: Int)

    // 프로젝트 수정
    @Query("UPDATE project SET project_title = :projectTitle, start_day = :startDay, end_day = :endDay WHERE project_code = :projectCode")
    fun editProject(projectCode: Int, projectTitle: String, startDay: String, endDay: String)

    // 목표 수정 (!!!!!일정 개수는 추후 수정 필요!!!!!)
    @Query("UPDATE target SET target_title = :targetTitle, todo_count = :todoCount WHERE target_code = :targetCode")
    fun editTarget(targetCode: Int, targetTitle: String, todoCount: Int)

    // 목표 TB에 일정 개수 수정
    @Query("UPDATE target SET todo_count = :todoCount WHERE target_code = :targetCode")
    fun editTargetTodoCount(targetCode:Int, todoCount: Int)

    // 일정 수정
    @Query("UPDATE todo SET todo_detail = :todoDetail WHERE todo_code = :todoCode")
    fun editTodo(todoCode: Int, todoDetail: String)

    // 일정 완료 값 수정
    @Query("UPDATE todo SET end_check = :endCheck WHERE todo_code = :todoCode")
    fun editTodoCheck(todoCode: Int, endCheck: Int)
}