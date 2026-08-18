package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.WalletDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.WalletEntity
import com.example.data.model.BudgetStatus
import com.example.data.model.BudgetWithSpending
import com.example.data.model.WalletWithBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.UUID

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val walletDao: WalletDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val allWallets: Flow<List<WalletEntity>> = walletDao.getAllWallets()
    val recurringTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecurringTransactions()

    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(limit)
    }

    /**
     * Combined reactive flow calculating real-time wallet balances based on transactions & initial balances
     */
    fun getWalletsWithBalance(): Flow<List<WalletWithBalance>> {
        return combine(allWallets, allTransactions) { wallets, transactions ->
            wallets.map { wallet ->
                // Incomes into this wallet
                val walletIncomes = transactions.filter { it.walletId == wallet.id && it.type == "INCOME" }.sumOf { it.amount }
                // Expenses out of this wallet
                val walletExpenses = transactions.filter { it.walletId == wallet.id && it.type == "EXPENSE" }.sumOf { it.amount }
                // Transfers out from this wallet
                val transfersOut = transactions.filter { it.walletId == wallet.id && it.type == "TRANSFER" }.sumOf { it.amount }
                // Transfers in to this wallet
                val transfersIn = transactions.filter { it.toWalletId == wallet.id && it.type == "TRANSFER" }.sumOf { it.amount }

                val currentBalance = wallet.initialBalance + walletIncomes - walletExpenses - transfersOut + transfersIn
                val txCount = transactions.count { it.walletId == wallet.id || it.toWalletId == wallet.id }

                WalletWithBalance(
                    id = wallet.id,
                    name = wallet.name,
                    type = wallet.type,
                    initialBalance = wallet.initialBalance,
                    currentBalance = currentBalance,
                    colorHex = wallet.colorHex,
                    iconKey = wallet.iconKey,
                    accountNumber = wallet.accountNumber,
                    isDefault = wallet.isDefault,
                    totalIncome = walletIncomes + transfersIn,
                    totalExpense = walletExpenses + transfersOut,
                    transactionCount = txCount
                )
            }
        }
    }

    /**
     * Combined reactive flow calculating budgets and spent amounts in current month
     */
    fun getBudgetsWithSpending(): Flow<List<BudgetWithSpending>> {
        return combine(allTransactions, allBudgets, allCategories) { transactions, budgets, categories ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis

            val monthlyExpenses = transactions.filter {
                it.type == "EXPENSE" && it.timestamp >= startOfMonth
            }

            val spentByCategory = monthlyExpenses.groupBy { it.categoryId }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }

            val catMap = categories.associateBy { it.id }

            budgets.map { budget ->
                val spent = spentByCategory[budget.categoryId] ?: 0L
                val category = catMap[budget.categoryId]
                val limit = if (budget.monthlyLimit > 0) budget.monthlyLimit else 1L
                val percentage = (spent.toFloat() / limit.toFloat()) * 100f
                val status = when {
                    percentage >= 100f -> BudgetStatus.EXCEEDED
                    percentage >= 75f -> BudgetStatus.WARNING
                    else -> BudgetStatus.SAFE
                }

                BudgetWithSpending(
                    categoryId = budget.categoryId,
                    categoryName = category?.name ?: "Category",
                    categoryIcon = category?.iconKey ?: "more_horiz",
                    categoryColor = category?.colorHex ?: "#64748B",
                    monthlyLimit = budget.monthlyLimit,
                    currentSpent = spent,
                    percentage = percentage,
                    status = status
                )
            }
        }
    }

    suspend fun addTransaction(
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
    ): String {
        val id = UUID.randomUUID().toString()
        val tx = TransactionEntity(
            id = id,
            title = title.trim(),
            amount = amount,
            type = type,
            categoryId = categoryId,
            categoryName = categoryName,
            categoryIcon = categoryIcon,
            categoryColor = categoryColor,
            timestamp = timestamp,
            note = note.trim(),
            isRecurring = isRecurring,
            frequency = frequency,
            nextDueDate = if (isRecurring && nextDueDate == 0L) calculateNextDueDate(frequency, timestamp) else nextDueDate,
            autoApply = autoApply,
            syncStatus = "PENDING",
            isDirty = true,
            updatedAt = System.currentTimeMillis(),
            walletId = walletId,
            walletName = walletName,
            toWalletId = toWalletId,
            toWalletName = toWalletName
        )
        transactionDao.insertTransaction(tx)
        return id
    }

    suspend fun transferBetweenWallets(
        fromWalletId: String,
        fromWalletName: String,
        toWalletId: String,
        toWalletName: String,
        amount: Long,
        note: String = "",
        timestamp: Long = System.currentTimeMillis()
    ): String {
        return addTransaction(
            title = "Transfer: $fromWalletName ➔ $toWalletName",
            amount = amount,
            type = "TRANSFER",
            categoryId = "cat_transfer",
            categoryName = "Transfer (ငွေလွှဲ)",
            categoryIcon = "account_balance",
            categoryColor = "#3B82F6",
            timestamp = timestamp,
            note = note,
            walletId = fromWalletId,
            walletName = fromWalletName,
            toWalletId = toWalletId,
            toWalletName = toWalletName
        )
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        val updated = transaction.copy(
            isDirty = true,
            syncStatus = "PENDING",
            updatedAt = System.currentTimeMillis()
        )
        transactionDao.updateTransaction(updated)
    }

    suspend fun deleteTransaction(id: String) {
        transactionDao.deleteTransaction(id)
    }

    suspend fun addWallet(
        name: String,
        type: String,
        initialBalance: Long = 0L,
        colorHex: String = "#0066B2",
        iconKey: String = "phone_iphone",
        accountNumber: String = "",
        isDefault: Boolean = false
    ): String {
        val id = "wallet_${UUID.randomUUID().toString().take(8)}"
        val wallet = WalletEntity(
            id = id,
            name = name.trim(),
            type = type,
            initialBalance = initialBalance,
            colorHex = colorHex,
            iconKey = iconKey,
            accountNumber = accountNumber.trim(),
            isDefault = isDefault,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        walletDao.insertWallet(wallet)
        return id
    }

    suspend fun updateWallet(wallet: WalletEntity) {
        walletDao.updateWallet(wallet.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteWallet(id: String) {
        walletDao.deleteWalletById(id)
    }

    suspend fun adjustWalletBalance(walletId: String, targetCurrentBalance: Long) {
        val wallet = walletDao.getWalletById(walletId) ?: return
        val transactions = transactionDao.getAllTransactions().first()
        val walletTxs = transactions.filter { it.walletId == walletId || it.toWalletId == walletId }
        val totalIncome = walletTxs.filter { it.type == "INCOME" || (it.type == "TRANSFER" && it.toWalletId == walletId) }.sumOf { it.amount }
        val totalExpense = walletTxs.filter { it.type == "EXPENSE" || (it.type == "TRANSFER" && it.walletId == walletId) }.sumOf { it.amount }
        val netChange = totalIncome - totalExpense
        val calculatedInitialBalance = targetCurrentBalance - netChange
        walletDao.updateWalletInitialBalance(walletId, calculatedInitialBalance)
    }

    suspend fun resetAllData() {
        transactionDao.deleteAllTransactions()
        budgetDao.deleteAllBudgets()
        walletDao.deleteAllWallets()
        categoryDao.deleteAllCategories()

        categoryDao.insertCategories(AppDatabase.DEFAULT_CATEGORIES)
        budgetDao.insertOrUpdateBudgets(AppDatabase.DEFAULT_BUDGETS)
        walletDao.insertWallets(AppDatabase.DEFAULT_WALLETS)
    }

    suspend fun setBudget(categoryId: String, limit: Long, monthYear: String = "DEFAULT") {
        val budget = BudgetEntity(
            categoryId = categoryId,
            monthlyLimit = limit,
            monthYear = monthYear,
            updatedAt = System.currentTimeMillis(),
            syncStatus = "PENDING",
            isDirty = true
        )
        budgetDao.insertOrUpdateBudget(budget)
    }

    suspend fun deleteBudget(categoryId: String) {
        budgetDao.deleteBudget(categoryId)
    }

    suspend fun addCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(id: String) {
        categoryDao.deleteCategory(id)
    }

    suspend fun applyRecurringTransaction(recurringTx: TransactionEntity) {
        // Create an executed transaction instance
        addTransaction(
            title = recurringTx.title,
            amount = recurringTx.amount,
            type = recurringTx.type,
            categoryId = recurringTx.categoryId,
            categoryName = recurringTx.categoryName,
            categoryIcon = recurringTx.categoryIcon,
            categoryColor = recurringTx.categoryColor,
            timestamp = System.currentTimeMillis(),
            note = "Auto-applied from subscription: ${recurringTx.title}",
            isRecurring = false,
            walletId = recurringTx.walletId,
            walletName = recurringTx.walletName
        )

        // Advance next due date
        val nextDue = calculateNextDueDate(recurringTx.frequency, recurringTx.nextDueDate)
        updateTransaction(recurringTx.copy(nextDueDate = nextDue))
    }

    private fun calculateNextDueDate(frequency: String, baseTime: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = if (baseTime > 0) baseTime else System.currentTimeMillis()
        when (frequency) {
            "DAILY" -> cal.add(Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> cal.add(Calendar.MONTH, 1)
            "YEARLY" -> cal.add(Calendar.YEAR, 1)
            else -> cal.add(Calendar.MONTH, 1)
        }
        return cal.timeInMillis
    }
}

