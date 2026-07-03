package com.example.todolist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY is_completed ASC, priority DESC, due_date ASC, created_at DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?

    @Query("SELECT * FROM todos WHERE category_id = :categoryId ORDER BY is_completed ASC, priority DESC, due_date ASC")
    fun getTodosByCategory(categoryId: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE is_completed = :completed ORDER BY priority DESC, due_date ASC")
    fun getTodosByCompletion(completed: Boolean): Flow<List<TodoEntity>>

    @Query("""
        SELECT * FROM todos 
        WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'
        ORDER BY is_completed ASC, priority DESC, created_at DESC
    """)
    fun searchTodos(query: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE priority = :priority ORDER BY is_completed ASC, due_date ASC")
    fun getTodosByPriority(priority: Int): Flow<List<TodoEntity>>

    @Query("SELECT COUNT(*) FROM todos WHERE is_completed = 0")
    fun getActiveCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE is_completed = 1")
    fun getCompletedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE is_completed = 1")
    suspend fun deleteCompletedTodos()
}
