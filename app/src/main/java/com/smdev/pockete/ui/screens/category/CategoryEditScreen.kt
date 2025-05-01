package com.smdev.pockete.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.smdev.pockete.data.model.Category

// Tailwind CSS colors
private val tailwindColors = listOf(
    Color(0xFFEF4444), // Red-500
    Color(0xFFF97316), // Orange-500
    Color(0xFFEAB308), // Yellow-500
    Color(0xFF22C55E), // Green-500
    Color(0xFF06B6D4), // Cyan-500
    Color(0xFF3B82F6), // Blue-500
    Color(0xFF8B5CF6), // Violet-500
    Color(0xFFEC4899), // Pink-500
    Color(0xFF6B7280), // Gray-500
    Color(0xFF000000), // Black
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(
    category: Category?,
    onSave: (String, Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var color by remember { mutableIntStateOf(category?.color ?: tailwindColors[0].toArgb()) }
    var showColorDropdown by remember { mutableStateOf(false) }

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

            // Color selection
            Column {
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedButton(
                    onClick = { showColorDropdown = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color = Color(color), shape = MaterialTheme.shapes.small)
                            )
                            Text(
                                text = tailwindColors.indexOfFirst { it.toArgb() == color }
                                    .let { "Color ${it + 1}" }
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select color"
                        )
                    }
                }
            }

            Button(
                onClick = { onSave(name, color) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text(if (category == null) "Add Category" else "Save Changes")
            }
        }
    }

    if (showColorDropdown) {
        AlertDialog(
            onDismissRequest = { showColorDropdown = false },
            title = { Text("Select Color") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tailwindColors) { colorOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    color = colorOption.toArgb()
                                    showColorDropdown = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color = colorOption, shape = MaterialTheme.shapes.small)
                            )
                            Text(
                                text = "Color ${tailwindColors.indexOf(colorOption) + 1}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorDropdown = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
