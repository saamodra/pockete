package com.smdev.pockete.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Template::class], version = 1, exportSchema = false)
abstract class TemplateDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: TemplateDatabase? = null

        fun getDatabase(context: Context): TemplateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TemplateDatabase::class.java,
                    "template_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
