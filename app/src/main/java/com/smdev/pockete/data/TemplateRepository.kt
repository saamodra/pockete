package com.smdev.pockete.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepository(
    private val templateDao: TemplateDao,
    private val templateCategoryDao: TemplateCategoryDao
) {
    val allTemplates: Flow<List<Template>> = templateDao.getAllTemplates()
        .map { templates ->
            templates
        }

    suspend fun getTemplateById(id: Long): TemplateWithCategories? {
        return templateDao.getTemplateWithCategories(id)
    }

    suspend fun insertTemplate(template: Template): Long {
        return templateDao.insertTemplate(template)
    }

    suspend fun updateTemplate(template: Template) {
        templateDao.updateTemplate(template)
    }

    suspend fun deleteTemplate(template: Template) {
        templateDao.deleteTemplate(template)
    }

    suspend fun addCategoryToTemplate(templateId: Long, categoryId: Long) {
        templateCategoryDao.insert(TemplateCategory(templateId, categoryId))
    }

    suspend fun removeCategoryFromTemplate(templateId: Long, categoryId: Long) {
        templateCategoryDao.delete(TemplateCategory(templateId, categoryId))
    }

    suspend fun getCategoryIdsForTemplate(templateId: Long): List<Long> {
        return templateCategoryDao.getCategoryIdsForTemplate(templateId)
    }
}
