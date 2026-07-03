package com.example.todolist

import android.app.Application
import com.example.todolist.data.database.AppDatabase
import com.example.todolist.data.network.QuoteApiService
import com.example.todolist.data.repository.CategoryRepository
import com.example.todolist.data.repository.QuoteRepository
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.datastore.UserPreferencesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TodoApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)
    }

    companion object {
        lateinit var instance: TodoApplication
            private set
    }
}

class AppContainer(context: TodoApplication) {

    // Database
    private val database = AppDatabase.getInstance(context)

    // DAOs
    private val todoDao = database.todoDao()
    private val categoryDao = database.categoryDao()

    // Repositories
    val todoRepository = TodoRepository(todoDao)
    val categoryRepository = CategoryRepository(categoryDao)

    // DataStore
    val userPreferencesRepository = UserPreferencesRepository(context)

    // Network
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.quotable.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val quoteApiService = retrofit.create(QuoteApiService::class.java)
    val quoteRepository = QuoteRepository(quoteApiService)
}
