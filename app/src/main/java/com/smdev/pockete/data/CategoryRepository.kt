package com.smdev.pockete.data

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    fun getCategoriesForTemplate(templateId: Long): Flow<List<Category>> {
        return categoryDao.getCategoriesForTemplate(templateId)
    }

    suspend fun addCategoryToTemplate(templateId: Long, categoryId: Long) {
        categoryDao.insertTemplateCategory(TemplateCategory(templateId, categoryId))
    }

    suspend fun removeCategoryFromTemplate(templateId: Long, categoryId: Long) {
        categoryDao.deleteTemplateCategory(TemplateCategory(templateId, categoryId))
    }
}
