package com.smdev.pockete.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smdev.pockete.data.dao.CategoryDao
import com.smdev.pockete.data.dao.WalletCategoryDao
import com.smdev.pockete.data.dao.WalletDao
import com.smdev.pockete.data.model.Category
import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.data.model.WalletCategory

@Database(
    entities = [
        Wallet::class,
        Category::class,
        WalletCategory::class
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun walletCategoryDao(): WalletCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pockete_database"
                )
                    .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
