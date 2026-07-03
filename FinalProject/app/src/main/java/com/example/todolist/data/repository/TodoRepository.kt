package com.example.todolist.data.repository

import com.example.todolist.data.dao.TodoDao
import com.example.todolist.data.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {

    fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()

    suspend fun getTodoById(id: Long): TodoEntity? = todoDao.getTodoById(id)

    fun getTodosByCategory(categoryId: Long): Flow<List<TodoEntity>> =
        todoDao.getTodosByCategory(categoryId)

    fun getTodosByCompletion(completed: Boolean): Flow<List<TodoEntity>> =
        todoDao.getTodosByCompletion(completed)

    fun searchTodos(query: String): Flow<List<TodoEntity>> = todoDao.searchTodos(query)

    fun getTodosByPriority(priority: Int): Flow<List<TodoEntity>> =
        todoDao.getTodosByPriority(priority)

    fun getActiveCount(): Flow<Int> = todoDao.getActiveCount()

    fun getCompletedCount(): Flow<Int> = todoDao.getCompletedCount()

    suspend fun insertTodo(todo: TodoEntity): Long = todoDao.insertTodo(todo)

    suspend fun updateTodo(todo: TodoEntity) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: TodoEntity) = todoDao.deleteTodo(todo)

    suspend fun deleteCompletedTodos() = todoDao.deleteCompletedTodos()
}
