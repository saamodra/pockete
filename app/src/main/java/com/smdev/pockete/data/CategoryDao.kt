package com.smdev.pockete.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?

    @Insert
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories c INNER JOIN template_categories tc ON c.id = tc.categoryId WHERE tc.templateId = :templateId")
    suspend fun getCategoriesForTemplate(templateId: Long): List<Category>

    @Insert
    suspend fun insertTemplateCategory(templateCategory: TemplateCategory)

    @Delete
    suspend fun deleteTemplateCategory(templateCategory: TemplateCategory)
}
