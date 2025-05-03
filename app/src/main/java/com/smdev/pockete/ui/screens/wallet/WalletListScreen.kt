package com.smdev.pockete.ui.screens.wallet

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smdev.pockete.data.fake.fakeCategoryRepository
import com.smdev.pockete.data.fake.fakeWalletRepository
import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.ui.components.WalletCard
import com.smdev.pockete.ui.screens.category.CategoryViewModel
import com.smdev.pockete.ui.screens.category.CategoryViewModelFactory
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Search
import compose.icons.tablericons.Trash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    viewModel: WalletViewModel,
    categoryViewModel: CategoryViewModel,
    onAddWallet: () -> Unit,
    onEditWallet: (Wallet) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<Wallet?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    val filteredWallet = remember(uiState.wallets, searchQuery, selectedCategoryId) {
        uiState.wallets.filter { walletWithCategories ->
            val matchesSearch = searchQuery.isBlank() ||
                    walletWithCategories.wallet.name.contains(searchQuery, ignoreCase = true) ||
                    walletWithCategories.wallet.number.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategoryId == null ||
                    walletWithCategories.categories.any { it.id == selectedCategoryId }

            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray.copy(alpha = 0.5f)),
                color = Color.White
            ) {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search wallets...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                        } else {
                            Text("Pockete")
                        }
                    },
                    actions = {
                        if (isSearchActive) {
                            IconButton(onClick = {
                                searchQuery = ""
                                isSearchActive = false
                            }) {
                                Icon(TablerIcons.Search, contentDescription = "Close Search")
                            }
                        } else {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(TablerIcons.Search, contentDescription = "Search")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color.Black
                    )
                )
            }
        },
        floatingActionButton = {
            if (!isSearchActive) {
                FloatingActionButton(onClick = onAddWallet) {
                    Icon(TablerIcons.Plus, contentDescription = "Add Template")
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    CategoryChip(
                        text = "All",
                        isSelected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null }
                    )
                    categories.forEach { category ->
                        CategoryChip(
                            text = category.name,
                            isSelected = selectedCategoryId == category.id,
                            onClick = {
                                selectedCategoryId =
                                    if (selectedCategoryId == category.id) null else category.id
                            }
                        )
                    }
                }
            }
            items(filteredWallet) { walletWithCategories ->
                SwipeToDeleteWallet(
                    wallet = walletWithCategories.wallet,
                    onDelete = {
                        viewModel.deleteWallet(walletWithCategories.wallet)
                        Toast.makeText(
                            context,
                            "${walletWithCategories.wallet.name} deleted!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                ) {
                    WalletCard(
                        wallet = walletWithCategories.wallet,
                        onCopy = {
                            viewModel.copyToClipboard(
                                context,
                                walletWithCategories.wallet.number
                            )
                        },
                        onShare = {
                            viewModel.shareWallet(
                                context,
                                walletWithCategories.wallet.number
                            )
                        },
                        onEdit = { onEditWallet(walletWithCategories.wallet) },
                        onDelete = { showDeleteDialog = walletWithCategories.wallet }
                    )
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Wallet") },
            text = { Text("Are you sure you want to delete this wallet?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteWallet(it) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteWallet(
    wallet: Wallet,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                showDeleteConfirmation = true
                false
            } else {
                false
            }
        }
    )

    LaunchedEffect(showDeleteConfirmation) {
        if (!showDeleteConfirmation) {
            dismissState.reset()
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = { Text("Delete Wallet") },
            text = { Text("Are you sure you want to delete ${wallet.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color = Color(0xFFFF4444)
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                color = color,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = TablerIcons.Trash,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        content()
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) Color.Black else Color.White
        ),
        modifier = Modifier.padding(end = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun WalletListScreenPreview() {
    val fakeWalletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(fakeWalletRepository)
    )
    val fakeCategoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(fakeCategoryRepository)
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            WalletListScreen(
                viewModel = fakeWalletViewModel,
                categoryViewModel = fakeCategoryViewModel,
                onAddWallet = {},
                onEditWallet = {}
            )
        }
    }
}
