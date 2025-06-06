package com.smdev.pockete.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smdev.pockete.data.model.Category
import compose.icons.TablerIcons
import compose.icons.tablericons.Pencil
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    categories: List<Category>,
    onAddCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                actions = {
                    IconButton(onClick = onAddCategory) {
                        Icon(imageVector = TablerIcons.Plus, contentDescription = "Add Category")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        onEdit = { onEditCategory(category) },
                        onDelete = { showDeleteDialog = category }
                    )
                }
            }

            if (showDeleteDialog != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    title = { Text("Delete Category") },
                    text = { Text("Are you sure you want to delete this category?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog?.let { onDeleteCategory(it) }
                                showDeleteDialog = null
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color = Color(category.color), shape = MaterialTheme.shapes.small)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(TablerIcons.Pencil, contentDescription = "Edit Category")
                }
                IconButton(onClick = onDelete) {
                    Icon(TablerIcons.Trash, contentDescription = "Delete Category")
                }
            }
        }
    }
}
