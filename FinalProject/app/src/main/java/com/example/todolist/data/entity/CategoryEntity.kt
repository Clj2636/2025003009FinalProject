package com.example.todolist.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val color: Long = 0xFF6750A4, // 默认颜色

    @ColumnInfo(name = "icon")
    val icon: String = "category" // Material Icons 名称
)
