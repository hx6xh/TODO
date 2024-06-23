package com.example.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.data.Todo
import com.example.todo.data.TodoDao

@Database(version = 5, entities = [Todo::class], exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun TodoDao(): TodoDao
}
