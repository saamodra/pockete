package com.smdev.pockete.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WalletWithCategories(
    @Embedded val wallet: Wallet,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WalletCategory::class,
            parentColumn = "walletId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<Category>
)