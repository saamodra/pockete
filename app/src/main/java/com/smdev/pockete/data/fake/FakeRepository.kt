package com.smdev.pockete.data.fake

import com.smdev.pockete.data.dao.WalletCategoryDao
import com.smdev.pockete.data.dao.WalletDao
import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.data.model.WalletCategory
import com.smdev.pockete.data.model.WalletWithCategories
import com.smdev.pockete.data.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val fakeWalletRepository = WalletRepository(
    object : WalletDao {
        override fun getAllWallets(): Flow<List<Wallet>> = flowOf(dummyWallets)
        override suspend fun insertWallet(wallet: Wallet): Long = 1
        override suspend fun updateWallet(wallet: Wallet) {}
        override suspend fun deleteWallet(wallet: Wallet) {}
        override suspend fun getWalletById(id: Long): Wallet? = null
        override suspend fun getWalletWithCategories(walletId: Long): WalletWithCategories? = null
    },
    object : WalletCategoryDao {
        override suspend fun insert(walletCategory: WalletCategory) {}
        override suspend fun delete(walletCategory: WalletCategory) {}
        override suspend fun getCategoryIdsForWallet(walletId: Long): List<Long> = emptyList()
        override suspend fun deleteAllForWallet(walletId: Long) {}
        override suspend fun deleteAllForCategory(categoryId: Long) {}
    }
)
