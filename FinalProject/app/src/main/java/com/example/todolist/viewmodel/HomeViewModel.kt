package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.TodoApplication
import com.example.todolist.data.entity.TodoEntity
import com.example.todolist.data.repository.CategoryRepository
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.model.TodoWithCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(FilterType.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val filteredTodos = _selectedFilter.flatMapLatest { filter ->
        _searchQuery.flatMapLatest { query ->
            when {
                query.isNotBlank() -> todoRepository.searchTodos(query)
                filter == FilterType.ACTIVE -> todoRepository.getTodosByCompletion(false)
                filter == FilterType.COMPLETED -> todoRepository.getTodosByCompletion(true)
                else -> todoRepository.getAllTodos()
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(
                filteredTodos,
                categoryRepository.getAllCategories(),
                todoRepository.getActiveCount(),
                todoRepository.getCompletedCount()
            ) { todos, categories, activeCount, completedCount ->
                val todosWithCategory = todos.map { todo ->
                    val category = categories.find { it.id == todo.categoryId }
                    TodoWithCategory(todo, category)
                }
                // 始终传递 Success 状态，包含分类信息
                HomeUiState.Success(
                    todos = todosWithCategory,
                    categories = categories,
                    activeCount = activeCount,
                    completedCount = completedCount,
                    selectedFilter = _selectedFilter.value,
                    searchQuery = _searchQuery.value
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(filter: FilterType) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTodoCompletion(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
        }
    }

    fun deleteCompletedTodos() {
        viewModelScope.launch {
            todoRepository.deleteCompletedTodos()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApplication.instance
                return HomeViewModel(
                    app.appContainer.todoRepository,
                    app.appContainer.categoryRepository
                ) as T
            }
        }
    }
}
