package com.smdev.pockete.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.smdev.pockete.data.model.Category
import com.smdev.pockete.data.model.WalletWithCategories
import com.smdev.pockete.ui.components.ColorInput
import com.smdev.pockete.ui.theme.TailwindColors
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowBack
import compose.icons.tablericons.ArrowLeft
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletEditScreen(
    modifier: Modifier = Modifier,
    walletWithCategories: WalletWithCategories? = null,
    categories: List<Category> = emptyList(),
    onSave: (String, String, String, Long?, Int, List<Category>) -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.wallet?.name ?: "") }
    var number by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.wallet?.number ?: "") }
    var cardHolder by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.wallet?.cardHolder ?: "") }
    var color by remember(walletWithCategories) { mutableIntStateOf(walletWithCategories?.wallet?.color ?: TailwindColors.allColors[0].toArgb()) }
    var selectedCategories by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.categories ?: emptyList()) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    // Expiry date state
    var month by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.wallet?.expiryDate?.let { date ->
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        "${calendar.get(Calendar.MONTH) + 1}".padStart(2, '0')
    } ?: "") }
    var year by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.wallet?.expiryDate?.let { date ->
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        calendar.get(Calendar.YEAR).toString()
    } ?: "") }
    var expiryDate by remember(walletWithCategories) { mutableStateOf(walletWithCategories?.wallet?.expiryDate) }

    // Dropdown state for month
    var isMonthDropdownExpanded by remember { mutableStateOf(false) }

    // Update expiryDate when month or year changes
    LaunchedEffect(month, year) {
        if (month.length == 2 && year.length == 4) {
            try {
                val monthInt = month.toInt()
                val yearInt = year.toInt()
                if (monthInt in 1..12 && yearInt >= Calendar.getInstance().get(Calendar.YEAR)) {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, yearInt)
                        set(Calendar.MONTH, monthInt - 1)
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    expiryDate = calendar.timeInMillis
                } else {
                    expiryDate = null
                }
            } catch (_: NumberFormatException) {
                expiryDate = null
            }
        } else {
            expiryDate = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (walletWithCategories == null) "Add Wallet" else "Edit Wallet") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(TablerIcons.ArrowLeft, contentDescription = "Back")
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
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            ColorInput(
                value = color,
                onValueChange = { color = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it },
                label = { Text("Card Holder") },
                modifier = Modifier.fillMaxWidth()
            )

            // Expiry date input
            Text(
                text = "Expiry Date",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Month Dropdown with label
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Month (MM)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = isMonthDropdownExpanded,
                        onExpandedChange = { isMonthDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = month.ifEmpty { "Select" },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMonthDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        )
                        ExposedDropdownMenu(
                            expanded = isMonthDropdownExpanded,
                            onDismissRequest = { isMonthDropdownExpanded = false }
                        ) {
                            (1..12).forEach { monthValue ->
                                DropdownMenuItem(
                                    text = { Text(monthValue.toString().padStart(2, '0')) },
                                    onClick = {
                                        month = monthValue.toString().padStart(2, '0')
                                        isMonthDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "/",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 24.dp)
                )

                // Year Input with label
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Year (YYYY)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = {
                            if (it.length <= 4) {
                                year = it
                            }
                        },
                        placeholder = { Text("2026") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Categories section
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium
            )

            // Selected categories as chips with Add Category button
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedCategories.forEach { category ->
                    CategoryChip(
                        category = category,
                        onRemove = {
                            selectedCategories = selectedCategories - category
                        }
                    )
                }

                // Add Category chip
                Surface(
                    modifier = Modifier.clickable { showCategoryDialog = true },
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add category",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Add Category",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onSave(name, number, cardHolder, expiryDate, color, selectedCategories)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && number.isNotBlank() && cardHolder.isNotBlank()
            ) {
                Text(if (walletWithCategories == null) "Add Wallet" else "Save Changes")
            }
        }
    }

    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Categories") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        CategorySelectionItem(
                            category = category,
                            isSelected = category in selectedCategories,
                            onToggle = {
                                selectedCategories = if (category in selectedCategories) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun CategoryChip(
    category: Category,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = Color(category.color).copy(alpha = 0.2f)
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

@Composable
fun CategorySelectionItem(
    category: Category,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
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
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
    }
}
