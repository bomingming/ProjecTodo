package com.example.projectodo

import android.view.inspector.IntFlagMapping
import androidx.annotation.CheckResult
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// project 테이블
@Entity(tableName = "project")
data class ProjectEntity (
    @PrimaryKey(autoGenerate = true) val project_code: Int,
    val project_title: String,
    val start_day: String,
    val end_day: String
    )

// target 테이블
@Entity(tableName = "target", foreignKeys = [ForeignKey(
    entity = ProjectEntity::class,
    parentColumns = ["project_code"],
    childColumns = ["project_code_for"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class TargetEntity(
    @PrimaryKey(autoGenerate = true) val target_code: Int,
    val project_code_for: Int,
    val target_title: String,
    val todo_count: Int
    )

// todo 테이블
@Entity(tableName = "todo", foreignKeys = [ForeignKey(
    entity = TargetEntity::class,
    parentColumns = ["target_code"],
    childColumns = ["target_code_for"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val todo_code: Int,
    val target_code_for: Int,
    val todo_detail: String,
    val end_check: Int
)
