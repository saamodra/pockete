package com.smdev.pockete.ui.screens.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smdev.pockete.data.model.Category
import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.data.model.WalletWithCategories
import com.smdev.pockete.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletUiState(
    val currentWallet: WalletWithCategories? = null,
    val wallets: List<WalletWithCategories> = emptyList(),
    val isEditing: Boolean = false
)

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {
    private val TAG = "WalletViewModel"
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
        viewModelScope.launch {
            Log.d(TAG, "Starting to collect wallets")
            repository.allWalletsWithCategories.collect { wallets ->
                Log.d(TAG, "Received wallet update: ${wallets.size} wallet")
                _uiState.update { currentState ->
                    val newState = currentState.copy(wallets = wallets)
                    Log.d(TAG, "Updated UI state with ${newState.wallets.size} wallet")
                    newState
                }
            }
        }
    }

    fun fetchWalletById(walletId: Long) {
        viewModelScope.launch {
            Log.d(TAG, "Fetching wallet by id: $walletId")
            val wallet = repository.getWalletById(walletId)
            Log.d(TAG, "Fetched wallet: ${wallet?.wallet?.name}")
            _uiState.update { currentState ->
                currentState.copy(
                    currentWallet = wallet,
                    isEditing = wallet != null
                )
            }
        }
    }

    fun clearCurrentWallet() {
        _uiState.update { currentState ->
            currentState.copy(
                currentWallet = null,
                isEditing = false
            )
        }
    }

    fun addWallet(title: String, content: String, cardHolder: String, expiryDate: Long?, color: Int, categories: List<Category>) {
        Log.d(TAG, "Adding new wallet: $title")
        viewModelScope.launch {
            val walletId = repository.insertWallet(Wallet(name = title, number = content, cardHolder = cardHolder, expiryDate = expiryDate, color = color))
            categories.forEach { category ->
                repository.addCategoryToWallet(walletId, category.id)
            }
            clearCurrentWallet()
        }
    }

    fun updateWallet(wallet: Wallet, categories: List<Category>) {
        Log.d(TAG, "Updating wallet: ${wallet.name}")
        viewModelScope.launch {
            repository.updateWallet(wallet)
            val currentCategoryIds = repository.getCategoryIdsForWallet(wallet.id)
            val newCategoryIds = categories.map { it.id }

            currentCategoryIds.filter { it !in newCategoryIds }.forEach { categoryId ->
                repository.removeCategoryFromWallet(wallet.id, categoryId)
            }

            newCategoryIds.filter { it !in currentCategoryIds }.forEach { categoryId ->
                repository.addCategoryToWallet(wallet.id, categoryId)
            }

            clearCurrentWallet()
        }
    }

    fun deleteWallet(wallet: Wallet) {
        Log.d(TAG, "Deleting wallet: ${wallet.name}")
        viewModelScope.launch {
            repository.deleteWallet(wallet)
            clearCurrentWallet()
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("wallet", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareWallet(context: Context, text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Wallet Number"))
    }
}
