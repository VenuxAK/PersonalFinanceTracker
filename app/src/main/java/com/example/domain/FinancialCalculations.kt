package com.example.domain

import com.example.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class FinancialSummary(
    val totalBalance: Long,
    val monthlyIncome: Long,
    val monthlyExpense: Long,
    val netSavings: Long,
    val savingsRate: Float, // 0 - 100%
    val pendingSyncCount: Int
)

data class CategoryExpenseBreakdown(
    val categoryId: String,
    val categoryName: String,
    val categoryColor: String,
    val categoryIcon: String,
    val totalAmount: Long,
    val percentage: Float
)

data class CashflowDataPoint(
    val label: String, // e.g. "Aug 10" or "Mon"
    val timestamp: Long,
    val income: Long,
    val expense: Long
)

object FinancialCalculations {

    fun calculateSummary(transactions: List<TransactionEntity>): FinancialSummary {
        val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val totalBalance = totalIncome - totalExpense

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = calendar.timeInMillis

        val currentMonthTxs = transactions.filter { it.timestamp >= startOfMonth }
        val monthlyIncome = currentMonthTxs.filter { it.type == "INCOME" }.sumOf { it.amount }
        val monthlyExpense = currentMonthTxs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val netSavings = monthlyIncome - monthlyExpense

        val savingsRate = if (monthlyIncome > 0) {
            ((netSavings.toFloat() / monthlyIncome.toFloat()) * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }

        val pendingSync = transactions.count { it.isDirty || it.syncStatus == "PENDING" }

        return FinancialSummary(
            totalBalance = totalBalance,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            netSavings = netSavings,
            savingsRate = savingsRate,
            pendingSyncCount = pendingSync
        )
    }

    fun calculateCategoryBreakdown(transactions: List<TransactionEntity>, monthFilterTimestamp: Long? = null): List<CategoryExpenseBreakdown> {
        val filtered = if (monthFilterTimestamp != null) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = monthFilterTimestamp
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val end = cal.timeInMillis
            transactions.filter { it.timestamp in start until end && it.type == "EXPENSE" }
        } else {
            transactions.filter { it.type == "EXPENSE" }
        }

        val totalExpense = filtered.sumOf { it.amount }
        if (totalExpense <= 0L) return emptyList()

        return filtered.groupBy { it.categoryId }
            .map { (catId, txs) ->
                val sample = txs.first()
                val sum = txs.sumOf { it.amount }
                val pct = (sum.toFloat() / totalExpense.toFloat()) * 100f
                CategoryExpenseBreakdown(
                    categoryId = catId,
                    categoryName = sample.categoryName,
                    categoryColor = sample.categoryColor,
                    categoryIcon = sample.categoryIcon,
                    totalAmount = sum,
                    percentage = pct
                )
            }
            .sortedByDescending { it.totalAmount }
    }

    /**
     * Generates 7 daily or periodic cash flow points for smooth bezier curve visual charts
     */
    fun generateCashflowSeries(transactions: List<TransactionEntity>, days: Int = 7): List<CashflowDataPoint> {
        val result = mutableListOf<CashflowDataPoint>()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

        for (i in (days - 1) downTo 0) {
            val dayCal = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val startOfDay = dayCal.timeInMillis
            val endOfDay = startOfDay + 86400000L - 1

            val dayTxs = transactions.filter { it.timestamp in startOfDay..endOfDay }
            val dayIncome = dayTxs.filter { it.type == "INCOME" }.sumOf { it.amount }
            val dayExpense = dayTxs.filter { it.type == "EXPENSE" }.sumOf { it.amount }

            result.add(
                CashflowDataPoint(
                    label = dateFormat.format(Date(startOfDay)),
                    timestamp = startOfDay,
                    income = dayIncome,
                    expense = dayExpense
                )
            )
        }
        return result
    }
}
