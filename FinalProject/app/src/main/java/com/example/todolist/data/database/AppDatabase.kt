package com.example.todolist.data.database

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.data.dao.CategoryDao
import com.example.todolist.data.dao.TodoDao
import com.example.todolist.data.entity.CategoryEntity
import com.example.todolist.data.entity.TodoEntity

@Database(
    entities = [TodoEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    .addCallback(PrepopulateCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PrepopulateCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 使用同步方式插入默认分类数据
                val categories = listOf(
                    ContentValues().apply {
                        put("id", 1L)
                        put("name", "工作")
                        put("color", 0xFF2196F3)
                        put("icon", "work")
                    },
                    ContentValues().apply {
                        put("id", 2L)
                        put("name", "学习")
                        put("color", 0xFF4CAF50)
                        put("icon", "school")
                    },
                    ContentValues().apply {
                        put("id", 3L)
                        put("name", "生活")
                        put("color", 0xFFFF9800)
                        put("icon", "home")
                    },
                    ContentValues().apply {
                        put("id", 4L)
                        put("name", "健康")
                        put("color", 0xFFE91E63)
                        put("icon", "favorite")
                    },
                    ContentValues().apply {
                        put("id", 5L)
                        put("name", "购物")
                        put("color", 0xFF9C27B0)
                        put("icon", "shopping_cart")
                    },
                    ContentValues().apply {
                        put("id", 6L)
                        put("name", "其他")
                        put("color", 0xFF607D8B)
                        put("icon", "more_horiz")
                    }
                )
                categories.forEach { values ->
                    db.insert("categories", 2, values) // 2 = CONFLICT_IGNORE
                }
            }
        }
    }
}
