package com.example.projectodo

import androidx.room.Entity
import androidx.room.PrimaryKey

// project 테이블
@Entity(tableName = "project")
data class ProjectodoEntity (
    @PrimaryKey(autoGenerate = true) val project_code: Int,
    val project_title: String,
    val start_day: String,
    val end_day: String
    )

