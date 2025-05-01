package com.smdev.pockete.ui.screens.wallet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smdev.pockete.data.fake.fakeWalletRepository
import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.ui.components.WalletCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    viewModel: WalletViewModel,
    onAddWallet: () -> Unit,
    onEditWallet: (Wallet) -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<Wallet?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredWallet = remember(uiState.wallets, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.wallets
        } else {
            uiState.wallets.filter { wallet ->
                wallet.name.contains(searchQuery, ignoreCase = true) ||
                        wallet.number.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
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
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                            Icon(Icons.Default.Search, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isSearchActive) {
                FloatingActionButton(onClick = onAddWallet) {
                    Icon(Icons.Default.Add, contentDescription = "Add Template")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(filteredWallet) { wallet ->
                WalletCard(
                    wallet = wallet,
                    onCopy = { viewModel.copyToClipboard(context, wallet.number) },
                    onShare = { viewModel.shareWallet(context, wallet.number) },
                    onEdit = { onEditWallet(wallet) },
                    onDelete = { showDeleteDialog = wallet }
                )
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

@Preview(showBackground = true)
@Composable
fun WalletListScreenPreview() {
    val fakeWalletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(fakeWalletRepository)
    )

    MaterialTheme {
        WalletListScreen(
            viewModel = fakeWalletViewModel,
            onAddWallet = {},
            onEditWallet = {}
        )
    }
}
