package com.smdev.pockete.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.smdev.pockete.data.model.Template
import com.smdev.pockete.data.model.TemplateWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    fun getAllTemplates(): Flow<List<Template>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: Template): Long

    @Update
    suspend fun updateTemplate(template: Template)

    @Delete
    suspend fun deleteTemplate(template: Template)

    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): Template?

    @Transaction
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateWithCategories(templateId: Long): TemplateWithCategories?
}
