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
