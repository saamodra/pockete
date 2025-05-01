package com.smdev.pockete.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "wallet_categories",
    primaryKeys = ["walletId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WalletCategory(
    val walletId: Long,
    val categoryId: Long
)
