package com.smdev.pockete.data.dao

import androidx.room.*
import com.smdev.pockete.data.model.TemplateCategory

@Dao
interface TemplateCategoryDao {
    @Query("SELECT categoryId FROM template_categories WHERE templateId = :templateId")
    suspend fun getCategoryIdsForTemplate(templateId: Long): List<Long>

    @Insert
    suspend fun insert(templateCategory: TemplateCategory)

    @Delete
    suspend fun delete(templateCategory: TemplateCategory)

    @Query("DELETE FROM template_categories WHERE templateId = :templateId")
    suspend fun deleteAllForTemplate(templateId: Long)

    @Query("DELETE FROM template_categories WHERE categoryId = :categoryId")
    suspend fun deleteAllForCategory(categoryId: Long)
}
