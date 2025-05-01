package com.smdev.pockete.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TemplateWithCategories(
    @Embedded val template: Template,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TemplateCategory::class,
            parentColumn = "templateId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<Category>
)