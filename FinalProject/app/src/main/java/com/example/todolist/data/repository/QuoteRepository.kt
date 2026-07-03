package com.example.todolist.data.repository

import com.example.todolist.data.network.QuoteApiService
import com.example.todolist.data.network.QuoteResponse

class QuoteRepository(private val apiService: QuoteApiService) {

    suspend fun getRandomQuote(): Result<QuoteResponse> {
        return try {
            val quotes = apiService.getRandomQuote()
            if (quotes.isNotEmpty()) {
                Result.success(quotes.first())
            } else {
                Result.failure(Exception("未获取到名言"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
