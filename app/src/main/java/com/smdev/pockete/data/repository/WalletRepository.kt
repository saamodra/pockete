package com.smdev.pockete.data.repository

import com.smdev.pockete.data.dao.WalletCategoryDao
import com.smdev.pockete.data.dao.WalletDao
import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.data.model.WalletCategory
import com.smdev.pockete.data.model.WalletWithCategories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalletRepository(
    private val walletDao: WalletDao,
    private val walletCategoryDao: WalletCategoryDao
) {
    val allWallets: Flow<List<Wallet>> = walletDao.getAllWallets()
        .map { templates ->
            templates
        }

    suspend fun getWalletById(id: Long): WalletWithCategories? {
        return walletDao.getWalletWithCategories(id)
    }

    suspend fun insertWallet(wallet: Wallet): Long {
        return walletDao.insertWallet(wallet)
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.updateWallet(wallet)
    }

    suspend fun deleteWallet(wallet: Wallet) {
        walletDao.deleteWallet(wallet)
    }

    suspend fun addCategoryToWallet(walletId: Long, categoryId: Long) {
        walletCategoryDao.insert(WalletCategory(walletId, categoryId))
    }

    suspend fun removeCategoryFromWallet(walletId: Long, categoryId: Long) {
        walletCategoryDao.delete(WalletCategory(walletId, categoryId))
    }

    suspend fun getCategoryIdsForWallet(walletId: Long): List<Long> {
        return walletCategoryDao.getCategoryIdsForWallet(walletId)
    }
}
