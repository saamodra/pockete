package com.smdev.pockete.data.fake

import com.smdev.pockete.data.model.Wallet

val dummyWallet = Wallet(
    id = 1,
    title = "BCA2",
    content = "0812386717236",
    createdAt = System.currentTimeMillis()
)

val dummyWallets = listOf<Wallet>(
    dummyWallet,
    Wallet(
        id = 2,
        title = "BRI",
        content = "3712379817239812",
        createdAt = System.currentTimeMillis()
    ),
    Wallet(
        id = 3,
        title = "Mandiri",
        content = "123123123123123",
        createdAt = System.currentTimeMillis()
    ),
)