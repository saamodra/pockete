package com.smdev.pockete.data.fake

import com.smdev.pockete.data.dao.CategoryDao
import com.smdev.pockete.data.model.Category
import com.smdev.pockete.data.model.WalletCategory
import com.smdev.pockete.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val fakeCategoryRepository = CategoryRepository(
    object : CategoryDao {
        private val categories = listOf(
            Category(id = 1, name = "Credit Card", color = 0xFFE57373.toInt()),
            Category(id = 2, name = "Debit Card", color = 0xFF81C784.toInt()),
            Category(id = 3, name = "E-Wallet", color = 0xFF64B5F6.toInt()),
            Category(id = 4, name = "Bank Account", color = 0xFFFFB74D.toInt()),
            Category(id = 5, name = "Crypto", color = 0xFFBA68C8.toInt())
        )

        override fun getAllCategories(): Flow<List<Category>> = flowOf(categories)
        override suspend fun getCategoryById(categoryId: Long): Category? = null
        override suspend fun insertCategory(category: Category): Long = 1
        override suspend fun updateCategory(category: Category) {}
        override suspend fun deleteCategory(category: Category) {}
        override suspend fun getCategoriesForTemplate(walletId: Long): List<Category> = emptyList()
        override suspend fun insertTemplateCategory(walletCategory: WalletCategory) {}
        override suspend fun deleteTemplateCategory(walletCategory: WalletCategory) {}
    }
)
