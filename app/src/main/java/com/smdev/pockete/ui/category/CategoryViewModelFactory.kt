package com.smdev.pockete.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smdev.pockete.data.CategoryDao
import com.smdev.pockete.data.CategoryRepository

class CategoryViewModelFactory(private val categoryDao: CategoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(CategoryRepository(categoryDao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
