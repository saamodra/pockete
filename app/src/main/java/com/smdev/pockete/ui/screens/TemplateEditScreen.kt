package com.smdev.pockete.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smdev.pockete.data.Category
import com.smdev.pockete.data.TemplateWithCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditScreen(
    templateWithCategories: TemplateWithCategories? = null,
    categories: List<Category> = emptyList(),
    onSave: (String, String, List<Category>) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember(templateWithCategories) {
        mutableStateOf(
            templateWithCategories?.template?.title ?: ""
        )
    }
    var content by remember(templateWithCategories) {
        mutableStateOf(
            templateWithCategories?.template?.content ?: ""
        )
    }
    var selectedCategories by remember(templateWithCategories) {
        mutableStateOf(
            templateWithCategories?.categories ?: emptyList()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (templateWithCategories == null) "Add Template" else "Edit Template") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 5
            )

            // Categories section
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium
            )

            // Selected categories as tags
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedCategories) { category ->
                    CategoryTag(
                        category = category,
                        onRemove = {
                            selectedCategories = selectedCategories - category
                        }
                    )
                }
            }

            // Available categories to add
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val availableCategories = categories.filter { it !in selectedCategories }
                items(availableCategories) { category ->
                    CategoryTag(
                        category = category,
                        onClick = {
                            selectedCategories = selectedCategories + category
                        }
                    )
                }
            }

            Button(
                onClick = {
                    onSave(title, content, selectedCategories)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text(if (templateWithCategories == null) "Add Template" else "Save Changes")
            }
        }
    }
}

@Composable
fun CategoryTag(
    category: Category,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color(category.color).copy(alpha = 0.2f),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium
            )
            if (onRemove != null) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove category",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
