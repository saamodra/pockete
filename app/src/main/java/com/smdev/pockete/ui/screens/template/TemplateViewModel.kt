package com.smdev.pockete.ui.screens.template

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smdev.pockete.data.model.Category
import com.smdev.pockete.data.model.Template
import com.smdev.pockete.data.repository.TemplateRepository
import com.smdev.pockete.data.model.TemplateWithCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TemplateUiState(
    val currentTemplate: TemplateWithCategories? = null,
    val templates: List<Template> = emptyList(),
    val isEditing: Boolean = false
)

class TemplateViewModel(private val repository: TemplateRepository) : ViewModel() {
    private val TAG = "TemplateViewModel"
    private val _uiState = MutableStateFlow(TemplateUiState())
    val uiState: StateFlow<TemplateUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
        viewModelScope.launch {
            Log.d(TAG, "Starting to collect templates")
            repository.allTemplates.collect { templates ->
                Log.d(TAG, "Received templates update: ${templates.size} templates")
                _uiState.update { currentState ->
                    val newState = currentState.copy(templates = templates)
                    Log.d(TAG, "Updated UI state with ${newState.templates.size} templates")
                    newState
                }
            }
        }
    }

    fun fetchTemplateById(templateId: Long) {
        viewModelScope.launch {
            Log.d(TAG, "Fetching template by id: $templateId")
            val template = repository.getTemplateById(templateId)
            Log.d(TAG, "Fetched template: ${template?.template?.title}")
            _uiState.update { currentState ->
                currentState.copy(
                    currentTemplate = template,
                    isEditing = template != null
                )
            }
        }
    }

    fun clearCurrentTemplate() {
        _uiState.update { currentState ->
            currentState.copy(
                currentTemplate = null,
                isEditing = false
            )
        }
    }

    fun addTemplate(title: String, content: String, categories: List<Category>) {
        Log.d(TAG, "Adding new template: $title")
        viewModelScope.launch {
            val templateId = repository.insertTemplate(Template(title = title, content = content))
            categories.forEach { category ->
                repository.addCategoryToTemplate(templateId, category.id)
            }
            clearCurrentTemplate()
        }
    }

    fun updateTemplate(template: Template, categories: List<Category>) {
        Log.d(TAG, "Updating template: ${template.title}")
        viewModelScope.launch {
            repository.updateTemplate(template)
            val currentCategoryIds = repository.getCategoryIdsForTemplate(template.id)
            val newCategoryIds = categories.map { it.id }

            currentCategoryIds.filter { it !in newCategoryIds }.forEach { categoryId ->
                repository.removeCategoryFromTemplate(template.id, categoryId)
            }

            newCategoryIds.filter { it !in currentCategoryIds }.forEach { categoryId ->
                repository.addCategoryToTemplate(template.id, categoryId)
            }

            clearCurrentTemplate()
        }
    }

    fun deleteTemplate(template: Template) {
        Log.d(TAG, "Deleting template: ${template.title}")
        viewModelScope.launch {
            repository.deleteTemplate(template)
            clearCurrentTemplate()
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("template", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareTemplate(context: Context, text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Template"))
    }
}
