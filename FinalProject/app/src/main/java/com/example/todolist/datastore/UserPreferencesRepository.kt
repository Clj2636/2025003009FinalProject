package com.example.todolist.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_DEFAULT_CATEGORY_ID = longPreferencesKey("default_category_id")
        private val KEY_DEFAULT_PRIORITY = intPreferencesKey("default_priority")
        private val KEY_LAST_SEARCH_QUERY = stringPreferencesKey("last_search_query")
        private val KEY_SORT_ORDER = stringPreferencesKey("sort_order") // "priority", "date", "category"
    }

    // 深色模式
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
        }
    }

    // 默认分类
    val defaultCategoryId: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_CATEGORY_ID] ?: 0L
    }

    suspend fun setDefaultCategoryId(categoryId: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_CATEGORY_ID] = categoryId
        }
    }

    // 默认优先级
    val defaultPriority: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_PRIORITY] ?: 0
    }

    suspend fun setDefaultPriority(priority: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_PRIORITY] = priority
        }
    }

    // 最近搜索词
    val lastSearchQuery: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_SEARCH_QUERY] ?: ""
    }

    suspend fun setLastSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_SEARCH_QUERY] = query
        }
    }

    // 排序方式
    val sortOrder: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SORT_ORDER] ?: "priority"
    }

    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SORT_ORDER] = order
        }
    }
}
