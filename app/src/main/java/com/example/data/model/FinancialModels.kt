package com.example.data.model

enum class TransactionType {
    EXPENSE,
    INCOME;

    fun isExpense(): Boolean = this == EXPENSE
    fun isIncome(): Boolean = this == INCOME
}

enum class SyncStatus {
    SYNCED,
    PENDING,
    FAILED
}

enum class RecurrenceFrequency(val displayName: String, val approxDays: Int) {
    NONE("None", 0),
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365)
}

enum class BudgetStatus {
    SAFE,       // < 75%
    WARNING,    // 75% - 99%
    EXCEEDED    // >= 100%
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val count: Int, val timestamp: Long = System.currentTimeMillis()) : SyncState()
    data class Error(val message: String) : SyncState()
}

data class CategoryItem(
    val id: String,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val type: TransactionType = TransactionType.EXPENSE,
    val isDefault: Boolean = true
)

data class BudgetWithSpending(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val monthlyLimit: Long,
    val currentSpent: Long,
    val percentage: Float,
    val status: BudgetStatus
)

enum class WalletType(val titleEn: String, val titleMm: String, val defaultIcon: String) {
    MOBILE_WALLET("Mobile Wallet", "မိုဘိုင်းပိုက်ဆံအိတ်", "phone_iphone"),
    BANK_ACCOUNT("Bank Account", "ဘဏ်အကောင့်", "account_balance"),
    CASH("Cash", "လက်ငင်းငွေ", "payments"),
    OTHER("Other", "အခြား", "account_balance_wallet")
}

data class WalletWithBalance(
    val id: String,
    val name: String,
    val type: String,
    val initialBalance: Long,
    val currentBalance: Long,
    val colorHex: String,
    val iconKey: String,
    val accountNumber: String = "",
    val isDefault: Boolean = false,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val transactionCount: Int = 0
)

data class WalletPreset(
    val id: String,
    val name: String,
    val type: String,
    val colorHex: String,
    val iconKey: String,
    val nameMm: String? = null,
    val defaultAccountNumber: String = ""
)

