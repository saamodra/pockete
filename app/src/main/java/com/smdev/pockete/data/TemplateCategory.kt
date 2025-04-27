package com.smdev.pockete.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "template_categories",
    primaryKeys = ["templateId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Template::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
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
data class TemplateCategory(
    val templateId: Long,
    val categoryId: Long
)
