package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.BudgetWithSpending
import com.example.data.model.SyncState
import com.example.domain.CashflowDataPoint
import com.example.domain.CurrencyFormatter
import com.example.domain.FinancialSummary
import com.example.domain.LocalAppLocalization
import com.example.ui.components.BudgetProgressBar
import com.example.ui.components.CustomCashFlowChart
import com.example.ui.components.TransactionCard
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.VividCoral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    summary: FinancialSummary,
    cashflowPoints: List<CashflowDataPoint>,
    recentTransactions: List<TransactionEntity>,
    budgets: List<BudgetWithSpending>,
    syncState: SyncState,
    onSyncClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onSeeAllTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = when (selectedFilter) {
        "INCOME" -> recentTransactions.filter { it.type == "INCOME" }
        "EXPENSE" -> recentTransactions.filter { it.type == "EXPENSE" }
        else -> recentTransactions
    }

    val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    val currentDateStr = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_balancea_logo),
                        contentDescription = "Balancea Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BALANCEA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (loc.isBurmese()) "ယနေ့ ငွေကြေးသုံးသပ်ချက်" else currentDateStr,
                            style = MaterialTheme.typography.titleLarge,
                            color = extraColors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Cloud Sync Status Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(extraColors.cardElevated)
                        .border(1.dp, extraColors.border, RoundedCornerShape(12.dp))
                        .clickable { onSyncClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_dashboard_sync"),
                    contentAlignment = Alignment.Center
                ) {
                    when (syncState) {
                        is SyncState.Syncing -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        is SyncState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Synced",
                                tint = ElectricEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Sync",
                                tint = extraColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hero Balance Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                extraColors.cardBackground,
                                extraColors.cardElevated
                            )
                        )
                    )
                    .border(1.dp, extraColors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp)
                    .testTag("card_net_balance")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = loc.t("Total Net Balance", "လက်ကျန်ငွေ စုစုပေါင်း"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = extraColors.textSecondary
                        )

                        if (summary.savingsRate > 0f) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = ElectricEmerald,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${summary.savingsRate.toInt()}% ${loc.t("Saved", "စုဆောင်း")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = CurrencyFormatter.formatMMK(summary.netBalance, loc.isBurmese()),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        ),
                        color = if (summary.netBalance >= 0) extraColors.textPrimary else VividCoral
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Income and Expense Dual Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Income Pill
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ElectricEmerald.copy(alpha = 0.1f))
                                .border(1.dp, ElectricEmerald.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(ElectricEmerald.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = ElectricEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = loc.t("Income", "အဝင်"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extraColors.textSecondary
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCompactMMK(summary.totalIncome, loc.isBurmese()),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricEmerald
                                    )
                                }
                            }
                        }

                        // Expense Pill
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VividCoral.copy(alpha = 0.1f))
                                .border(1.dp, VividCoral.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(VividCoral.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = VividCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = loc.t("Expense", "အသုံး"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extraColors.textSecondary
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCompactMMK(summary.totalExpense, loc.isBurmese()),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = VividCoral
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cashflow Chart Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(extraColors.cardBackground)
                    .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = loc.t("7-Day Cash Flow Trend", "၇ ရက် ငွေစီးဆင်းမှု လမ်းကြောင်း"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = extraColors.textPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ElectricEmerald)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = loc.t("In", "ဝင်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textSecondary
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(VividCoral)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = loc.t("Out", "ထွက်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomCashFlowChart(
                        dataPoints = cashflowPoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }
        }

        // Active Budgets Quick Glance
        if (budgets.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = loc.t("Monthly Budgets", "လစဉ် ကန့်သတ်ငွေများ"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    budgets.take(2).forEach { budget ->
                        BudgetProgressBar(
                            budget = budget,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Recent Transactions Header & Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = loc.t("Recent Transactions", "လတ်တလော စာရင်းများ"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.textPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "ALL" to loc.t("All", "အားလုံး"),
                        "EXPENSE" to loc.t("Exp", "အသုံး"),
                        "INCOME" to loc.t("Inc", "အဝင်")
                    ).forEach { (filterKey, filterLabel) ->
                        val isSelected = selectedFilter == filterKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else extraColors.cardElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else extraColors.border,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = filterKey }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("filter_$filterKey")
                        ) {
                            Text(
                                text = filterLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Transactions List
        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(extraColors.cardBackground)
                        .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = loc.t("No transactions yet", "မှတ်တမ်း မရှိသေးပါ"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = extraColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loc.t("Tap + below to record your first income or expense", "ပထမဆုံး ဝင်ငွေ/ထွက်ငွေ မှတ်တမ်းတင်ရန် အောက်ရှိ + ကိုနှိပ်ပါ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { tx ->
                TransactionCard(
                    transaction = tx,
                    onClick = { onTransactionClick(tx) },
                    modifier = Modifier.testTag("tx_card_${tx.id}")
                )
            }
        }
    }
}
