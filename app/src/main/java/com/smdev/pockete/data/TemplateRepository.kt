package com.smdev.pockete.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepository(private val templateDao: TemplateDao) {
    val allTemplates: Flow<List<Template>> = templateDao.getAllTemplates()
        .map { templates ->
            templates
        }

    suspend fun insertTemplate(template: Template) {
        templateDao.insertTemplate(template)
    }

    suspend fun updateTemplate(template: Template) {
        templateDao.updateTemplate(template)
    }

    suspend fun deleteTemplate(template: Template) {
        templateDao.deleteTemplate(template)
    }

    suspend fun getTemplateById(id: Long): Template? {
        val template = templateDao.getTemplateById(id)
        return template
    }
}
