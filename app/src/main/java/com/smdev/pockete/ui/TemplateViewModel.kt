package com.smdev.pockete.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smdev.pockete.data.Template
import com.smdev.pockete.data.TemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun fetchTemplateById(id: Long) {
        viewModelScope.launch {
            Log.d(TAG, "Fetching template by id: $id")
            val template = repository.getTemplateById(id)
            Log.d(TAG, "Fetched template: ${template?.title}")
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

    fun addTemplate(title: String, content: String) {
        Log.d(TAG, "Adding new template: $title")
        viewModelScope.launch {
            repository.insertTemplate(Template(title = title, content = content))
            clearCurrentTemplate()
        }
    }

    fun updateTemplate(template: Template) {
        Log.d(TAG, "Updating template: ${template.title}")
        viewModelScope.launch {
            repository.updateTemplate(template)
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

data class TemplateUiState(
    val templates: List<Template> = emptyList(),
    val currentTemplate: Template? = null,
    val isEditing: Boolean = false
)
