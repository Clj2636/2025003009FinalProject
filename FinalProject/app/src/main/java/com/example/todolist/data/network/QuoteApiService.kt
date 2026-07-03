package com.example.todolist.data.network

import retrofit2.http.GET

interface QuoteApiService {

    @GET("random")
    suspend fun getRandomQuote(): List<QuoteResponse>
}

data class QuoteResponse(
    val _id: String,
    val content: String,
    val author: String,
    val tags: List<String>
)
