package com.smdev.pockete.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val number: String,
    val cardHolder: String,
    val color: Int = 0xFF000000.toInt(), // Default to black
    val expiryDate: Long? = null, // Unix timestamp in milliseconds, null if no expiry date
    val createdAt: Long = System.currentTimeMillis(),
)
