package com.smdev.pockete.data.dao

import androidx.room.*
import com.smdev.pockete.data.model.WalletCategory

@Dao
interface WalletCategoryDao {
    @Query("SELECT categoryId FROM wallet_categories WHERE walletId = :walletId")
    suspend fun getCategoryIdsForWallet(walletId: Long): List<Long>

    @Insert
    suspend fun insert(walletCategory: WalletCategory)

    @Delete
    suspend fun delete(walletCategory: WalletCategory)

    @Query("DELETE FROM wallet_categories WHERE walletId = :walletId")
    suspend fun deleteAllForWallet(walletId: Long)

    @Query("DELETE FROM wallet_categories WHERE categoryId = :categoryId")
    suspend fun deleteAllForCategory(categoryId: Long)
}
