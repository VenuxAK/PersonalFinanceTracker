package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Long, // in MMK
    val type: String, // "INCOME" or "EXPENSE"
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val isRecurring: Boolean = false,
    val frequency: String = "NONE", // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    val nextDueDate: Long = 0L,
    val autoApply: Boolean = false,
    val syncStatus: String = "PENDING", // "SYNCED", "PENDING", "FAILED"
    val isDirty: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val walletId: String? = null,
    val walletName: String? = null,
    val toWalletId: String? = null, // for TRANSFER type
    val toWalletName: String? = null
)
