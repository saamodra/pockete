package com.smdev.pockete.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Template::class,
        Category::class,
        TemplateCategory::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
    abstract fun categoryDao(): CategoryDao
    abstract fun templateCategoryDao(): TemplateCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pockete_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
