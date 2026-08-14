package com.example.data.repository

import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.BudgetStatus
import com.example.data.model.BudgetWithSpending
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import java.util.UUID

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val recurringTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecurringTransactions()

    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(limit)
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
        autoApply: Boolean = false
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
            updatedAt = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(tx)
        return id
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
            isRecurring = false
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
