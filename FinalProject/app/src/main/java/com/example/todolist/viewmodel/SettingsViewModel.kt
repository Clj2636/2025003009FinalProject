package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.TodoApplication
import com.example.todolist.data.repository.CategoryRepository
import com.example.todolist.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(
        SettingsUiState.Success()
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.isDarkMode,
                userPreferencesRepository.defaultPriority,
                userPreferencesRepository.defaultCategoryId,
                userPreferencesRepository.sortOrder,
                categoryRepository.getAllCategories()
            ) { darkMode, priority, categoryId, sortOrder, categories ->
                SettingsUiState.Success(
                    isDarkMode = darkMode,
                    defaultPriority = priority,
                    defaultCategoryId = categoryId,
                    sortOrder = sortOrder,
                    categories = categories
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkMode(enabled)
        }
    }

    fun setDefaultPriority(priority: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setDefaultPriority(priority)
        }
    }

    fun setDefaultCategoryId(categoryId: Long) {
        viewModelScope.launch {
            userPreferencesRepository.setDefaultCategoryId(categoryId)
        }
    }

    fun setSortOrder(order: String) {
        viewModelScope.launch {
            userPreferencesRepository.setSortOrder(order)
        }
    }

    fun setLastSearchQuery(query: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLastSearchQuery(query)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApplication.instance
                return SettingsViewModel(
                    app.appContainer.userPreferencesRepository,
                    app.appContainer.categoryRepository
                ) as T
            }
        }
    }
}
