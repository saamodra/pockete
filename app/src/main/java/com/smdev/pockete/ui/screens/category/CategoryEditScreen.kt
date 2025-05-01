package com.smdev.pockete.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.smdev.pockete.data.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(
    category: Category?,
    onSave: (String, Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var color by remember { mutableIntStateOf(category?.color ?: Color.Blue.toArgb()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (category == null) "Add Category" else "Edit Category") },
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Color picker would go here
            // For now, we'll just show the current color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color(color), shape = MaterialTheme.shapes.medium)
            )

            Button(
                onClick = { onSave(name, color) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text(if (category == null) "Add Category" else "Save Changes")
            }
        }
    }
}
