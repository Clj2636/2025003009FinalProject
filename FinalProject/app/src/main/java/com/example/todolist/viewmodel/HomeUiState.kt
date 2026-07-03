package com.example.todolist.viewmodel

import com.example.todolist.data.entity.CategoryEntity
import com.example.todolist.data.entity.TodoEntity
import com.example.todolist.model.TodoWithCategory

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val todos: List<TodoWithCategory>,
        val categories: List<CategoryEntity>,
        val activeCount: Int = 0,
        val completedCount: Int = 0,
        val selectedFilter: FilterType = FilterType.ALL,
        val searchQuery: String = ""
    ) : HomeUiState
    data object Empty : HomeUiState
}

enum class FilterType(val label: String) {
    ALL("全部"),
    ACTIVE("进行中"),
    COMPLETED("已完成")
}

sealed interface EditUiState {
    data object Idle : EditUiState
    data class Editing(
        val title: String = "",
        val description: String = "",
        val priority: Int = 0,
        val categoryId: Long = 0,
        val dueDate: Long = 0,
        val titleError: String? = null,
        val categories: List<CategoryEntity> = emptyList(),
        val isSaving: Boolean = false,
        val isExisting: Boolean = false
    ) : EditUiState
    data object Saved : EditUiState
    data class Error(val message: String) : EditUiState
}

sealed interface QuoteUiState {
    data object Loading : QuoteUiState
    data class Success(
        val content: String,
        val author: String,
        val tags: List<String>
    ) : QuoteUiState
    data class Error(val message: String) : QuoteUiState
}

sealed interface SettingsUiState {
    data class Success(
        val isDarkMode: Boolean = false,
        val defaultPriority: Int = 0,
        val defaultCategoryId: Long = 0,
        val sortOrder: String = "priority",
        val categories: List<CategoryEntity> = emptyList()
    ) : SettingsUiState
}
