package com.smdev.pockete.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.smdev.pockete.data.model.Category
import com.smdev.pockete.ui.components.ColorInput
import com.smdev.pockete.ui.theme.TailwindColors
import compose.icons.FeatherIcons
import compose.icons.TablerIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.Plus
import compose.icons.feathericons.Save
import compose.icons.feathericons.X
import compose.icons.tablericons.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(
    category: Category?,
    onSave: (String, Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var color by remember { mutableIntStateOf(category?.color ?: TailwindColors.allColors[0].toArgb()) }
    var showColorDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (category == null) "Add Category" else "Edit Category") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(FeatherIcons.X, contentDescription = "Back")
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
                leadingIcon = { Icon(TablerIcons.Tag, contentDescription = "Category") },
                modifier = Modifier.fillMaxWidth()
            )

            ColorInput(
                value = color,
                onValueChange = { color = it },
                label = "Category Color",
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSave(name, color) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (category == null) FeatherIcons.Plus else FeatherIcons.Save,
                        contentDescription = null
                    )
                    Text(if (category == null) "Add Category" else "Save Changes")
                }
            }
        }
    }
}
