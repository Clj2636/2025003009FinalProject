package com.example.todolist.data.repository

import com.example.todolist.data.dao.CategoryDao
import com.example.todolist.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): CategoryEntity? = categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    suspend fun deleteCategory(id: Long) = categoryDao.deleteCategory(id)
}
