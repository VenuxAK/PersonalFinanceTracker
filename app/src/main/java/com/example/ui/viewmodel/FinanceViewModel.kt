package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.BudgetWithSpending
import com.example.data.model.SyncState
import com.example.data.remote.FirestoreSyncService
import com.example.data.repository.FinanceRepository
import com.example.data.repository.SyncRepository
import com.example.domain.CategoryExpenseBreakdown
import com.example.domain.CashflowDataPoint
import com.example.domain.FinancialCalculations
import com.example.domain.FinancialSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val firestoreSyncService = FirestoreSyncService()
    val financeRepository = FinanceRepository(
        transactionDao = database.transactionDao(),
        budgetDao = database.budgetDao(),
        categoryDao = database.categoryDao(),
        walletDao = database.walletDao()
    )
    val syncRepository = SyncRepository(
        transactionDao = database.transactionDao(),
        budgetDao = database.budgetDao(),
        firestoreSyncService = firestoreSyncService
    )

    val allTransactions: StateFlow<List<TransactionEntity>> = financeRepository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentTransactions: StateFlow<List<TransactionEntity>> = financeRepository.getRecentTransactions(8)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allWallets: StateFlow<List<com.example.data.local.entity.WalletEntity>> = financeRepository.allWallets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val walletsWithBalance: StateFlow<List<com.example.data.model.WalletWithBalance>> = financeRepository.getWalletsWithBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<CategoryEntity>> = financeRepository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val budgetsWithSpending: StateFlow<List<BudgetWithSpending>> = financeRepository.getBudgetsWithSpending()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recurringTransactions: StateFlow<List<TransactionEntity>> = financeRepository.recurringTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val syncState: StateFlow<SyncState> = syncRepository.syncState

    val financialSummary: StateFlow<FinancialSummary> = allTransactions.map { txs ->
        FinancialCalculations.calculateSummary(txs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialSummary(0, 0, 0, 0, 0f, 0)
    )

    val categoryBreakdown: StateFlow<List<CategoryExpenseBreakdown>> = allTransactions.map { txs ->
        FinancialCalculations.calculateCategoryBreakdown(txs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cashflowPoints: StateFlow<List<CashflowDataPoint>> = allTransactions.map { txs ->
        FinancialCalculations.generateCashflowSeries(txs, 7)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(
        title: String,
        amount: Long,
        type: String,
        categoryId: String,
        categoryName: String,
        categoryIcon: String,
        categoryColor: String,
        timestamp: Long = System.currentTimeMillis(),
        note: String = "",
        isRecurring: Boolean = false,
        frequency: String = "NONE",
        nextDueDate: Long = 0L,
        autoApply: Boolean = false,
        walletId: String? = null,
        walletName: String? = null,
        toWalletId: String? = null,
        toWalletName: String? = null
    ) {
        viewModelScope.launch {
            financeRepository.addTransaction(
                title = title,
                amount = amount,
                type = type,
                categoryId = categoryId,
                categoryName = categoryName,
                categoryIcon = categoryIcon,
                categoryColor = categoryColor,
                timestamp = timestamp,
                note = note,
                isRecurring = isRecurring,
                frequency = frequency,
                nextDueDate = nextDueDate,
                autoApply = autoApply,
                walletId = walletId,
                walletName = walletName,
                toWalletId = toWalletId,
                toWalletName = toWalletName
            )
        }
    }

    fun transferFunds(
        fromWalletId: String,
        fromWalletName: String,
        toWalletId: String,
        toWalletName: String,
        amount: Long,
        note: String = "",
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            financeRepository.transferBetweenWallets(
                fromWalletId = fromWalletId,
                fromWalletName = fromWalletName,
                toWalletId = toWalletId,
                toWalletName = toWalletName,
                amount = amount,
                note = note,
                timestamp = timestamp
            )
        }
    }

    fun addWallet(
        name: String,
        type: String,
        initialBalance: Long = 0L,
        colorHex: String = "#0066B2",
        iconKey: String = "phone_iphone",
        accountNumber: String = "",
        isDefault: Boolean = false
    ) {
        viewModelScope.launch {
            financeRepository.addWallet(
                name = name,
                type = type,
                initialBalance = initialBalance,
                colorHex = colorHex,
                iconKey = iconKey,
                accountNumber = accountNumber,
                isDefault = isDefault
            )
        }
    }

    fun updateWallet(wallet: com.example.data.local.entity.WalletEntity) {
        viewModelScope.launch {
            financeRepository.updateWallet(wallet)
        }
    }

    fun deleteWallet(id: String) {
        viewModelScope.launch {
            financeRepository.deleteWallet(id)
        }
    }

    fun adjustWalletBalance(walletId: String, newBalance: Long) {
        viewModelScope.launch {
            financeRepository.adjustWalletBalance(walletId, newBalance)
        }
    }

    fun resetAllData(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            financeRepository.resetAllData()
            onComplete?.invoke()
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            financeRepository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            financeRepository.deleteTransaction(id)
        }
    }

    fun setBudget(categoryId: String, limit: Long) {
        viewModelScope.launch {
            financeRepository.setBudget(categoryId, limit)
        }
    }

    fun deleteBudget(categoryId: String) {
        viewModelScope.launch {
            financeRepository.deleteBudget(categoryId)
        }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            financeRepository.addCategory(category)
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            financeRepository.updateCategory(category)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            financeRepository.deleteCategory(id)
        }
    }

    fun applyRecurringTransaction(recurringTx: TransactionEntity) {
        viewModelScope.launch {
            financeRepository.applyRecurringTransaction(recurringTx)
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            syncRepository.syncToCloud()
        }
    }

    fun resetSyncState() {
        syncRepository.resetState()
    }
}
