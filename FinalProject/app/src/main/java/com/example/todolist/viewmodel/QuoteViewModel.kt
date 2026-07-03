package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.TodoApplication
import com.example.todolist.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuoteUiState>(QuoteUiState.Loading)
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    init {
        fetchRandomQuote()
    }

    fun fetchRandomQuote() {
        _uiState.value = QuoteUiState.Loading
        viewModelScope.launch {
            quoteRepository.getRandomQuote().fold(
                onSuccess = { quote ->
                    _uiState.value = QuoteUiState.Success(
                        content = quote.content,
                        author = quote.author,
                        tags = quote.tags
                    )
                },
                onFailure = { e ->
                    _uiState.value = QuoteUiState.Error(
                        e.message ?: "获取名言失败，请重试"
                    )
                }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApplication.instance
                return QuoteViewModel(app.appContainer.quoteRepository) as T
            }
        }
    }
}
