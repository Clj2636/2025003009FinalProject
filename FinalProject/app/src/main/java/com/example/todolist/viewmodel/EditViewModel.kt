package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.TodoApplication
import com.example.todolist.data.entity.TodoEntity
import com.example.todolist.data.repository.CategoryRepository
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditUiState>(EditUiState.Idle)
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    fun loadForNew(defaultCategoryId: Long = 0, defaultPriority: Int = 0) {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategories().first()
            _uiState.update {
                EditUiState.Editing(
                    categoryId = defaultCategoryId,
                    priority = defaultPriority,
                    categories = categories
                )
            }
        }
    }

    fun loadForEdit(todoId: Long) {
        viewModelScope.launch {
            val todo = todoRepository.getTodoById(todoId)
            val categories = categoryRepository.getAllCategories().first()
            if (todo != null) {
                _uiState.update {
                    EditUiState.Editing(
                        title = todo.title,
                        description = todo.description,
                        priority = todo.priority,
                        categoryId = todo.categoryId,
                        dueDate = todo.dueDate,
                        categories = categories,
                        isExisting = true
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        val current = _uiState.value
        if (current is EditUiState.Editing) {
            _uiState.value = current.copy(title = title, titleError = null)
        }
    }

    fun updateDescription(description: String) {
        val current = _uiState.value
        if (current is EditUiState.Editing) {
            _uiState.value = current.copy(description = description)
        }
    }

    fun updatePriority(priority: Int) {
        val current = _uiState.value
        if (current is EditUiState.Editing) {
            _uiState.value = current.copy(priority = priority)
        }
    }

    fun updateCategory(categoryId: Long) {
        val current = _uiState.value
        if (current is EditUiState.Editing) {
            _uiState.value = current.copy(categoryId = categoryId)
        }
    }

    fun updateDueDate(dueDate: Long) {
        val current = _uiState.value
        if (current is EditUiState.Editing) {
            _uiState.value = current.copy(dueDate = dueDate)
        }
    }

    fun saveTodo(todoId: Long = 0) {
        val current = _uiState.value
        if (current !is EditUiState.Editing) return

        // 验证标题
        if (current.title.isBlank()) {
            _uiState.value = current.copy(titleError = "标题不能为空")
            return
        }

        _uiState.value = current.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val todo = TodoEntity(
                    id = todoId,
                    title = current.title.trim(),
                    description = current.description.trim(),
                    priority = current.priority,
                    categoryId = current.categoryId,
                    dueDate = current.dueDate,
                    isCompleted = if (todoId != 0L) {
                        todoRepository.getTodoById(todoId)?.isCompleted ?: false
                    } else false,
                    createdAt = if (todoId != 0L) {
                        todoRepository.getTodoById(todoId)?.createdAt ?: System.currentTimeMillis()
                    } else System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                if (todoId == 0L) {
                    todoRepository.insertTodo(todo)
                } else {
                    todoRepository.updateTodo(todo)
                }
                _uiState.value = EditUiState.Saved
            } catch (e: Exception) {
                _uiState.value = EditUiState.Error(e.message ?: "保存失败")
            }
        }
    }

    fun resetState() {
        _uiState.value = EditUiState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApplication.instance
                return EditViewModel(
                    app.appContainer.todoRepository,
                    app.appContainer.categoryRepository
                ) as T
            }
        }
    }
}
