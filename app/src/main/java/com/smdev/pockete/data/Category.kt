package com.smdev.pockete.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: Int, // Store color as Int (ARGB)
    val createdAt: Long = System.currentTimeMillis()
)
