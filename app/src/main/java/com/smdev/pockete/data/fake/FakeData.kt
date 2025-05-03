package com.smdev.pockete.data.fake

import com.smdev.pockete.data.model.Wallet
import com.smdev.pockete.data.model.WalletWithCategories

val dummyWallet = Wallet(
    id = 1,
    name = "BCA2",
    number = "0812386717236",
    expiryDate = 1678901234567,
    cardHolder = "John Doe",
    createdAt = System.currentTimeMillis()
)

val dummyWallets = listOf<Wallet>(
    dummyWallet,
    Wallet(
        id = 2,
        name = "BRI",
        number = "3712379817239812",
        cardHolder = "Jane Doe",
        createdAt = System.currentTimeMillis()
    ),
    Wallet(
        id = 3,
        name = "Mandiri",
        number = "123123123123123",
        cardHolder = "John Smith",
        createdAt = System.currentTimeMillis()
    ),
)

val dummyWalletsWithCategories = listOf<WalletWithCategories>(
    WalletWithCategories(
        wallet = dummyWallet,
        categories = emptyList()
    ),
    WalletWithCategories(
        wallet = Wallet(
            id = 2,
            name = "BRI",
            number = "3712379817239812",
            cardHolder = "Jane Doe",
            createdAt = System.currentTimeMillis(),
        ),
        categories = emptyList()
    ),
    WalletWithCategories(
        wallet = Wallet(
            id = 3,
            name = "Mandiri",
            number = "123123123123123",
            cardHolder = "John Smith",
            createdAt = System.currentTimeMillis(),
        ),
                categories = emptyList()
    ),
)
