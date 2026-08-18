package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,                    // e.g. "KBZPay", "CB Pay", "Cash (လက်ငင်းငွေ)", "AYA Bank"
    val type: String,                    // "MOBILE_WALLET", "BANK_ACCOUNT", "CASH", "OTHER"
    val initialBalance: Long = 0L,       // in MMK (Ks)
    val colorHex: String = "#0066B2",     // Brand color Hex (e.g. KBZ Blue #0066B2)
    val iconKey: String = "phone_iphone",// Material icon key
    val accountNumber: String = "",       // Optional account number / phone / note (e.g. "09789...", "••• 4523")
    val isDefault: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
