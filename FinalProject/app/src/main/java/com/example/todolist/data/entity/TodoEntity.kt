package com.example.todolist.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "priority")
    val priority: Int = 0, // 0=低, 1=中, 2=高

    @ColumnInfo(name = "category_id")
    val categoryId: Long = 0, // 0 表示未分类

    @ColumnInfo(name = "due_date")
    val dueDate: Long = 0, // 截止日期时间戳，0 表示无截止日期

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
