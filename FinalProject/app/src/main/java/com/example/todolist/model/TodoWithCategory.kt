package com.example.todolist.model

import com.example.todolist.data.entity.CategoryEntity
import com.example.todolist.data.entity.TodoEntity

data class TodoWithCategory(
    val todo: TodoEntity,
    val category: CategoryEntity?
)

enum class Priority(val value: Int, val label: String) {
    LOW(0, "低"),
    MEDIUM(1, "中"),
    HIGH(2, "高");

    companion object {
        fun fromValue(value: Int): Priority = entries.find { it.value == value } ?: LOW
    }
}

enum class SortOrder(val key: String, val label: String) {
    PRIORITY("priority", "按优先级"),
    DATE("date", "按日期"),
    CATEGORY("category", "按分类");
}
