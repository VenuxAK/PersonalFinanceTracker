package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.BudgetStatus
import com.example.data.model.BudgetWithSpending
import com.example.data.model.SyncState
import com.example.domain.CashflowDataPoint
import com.example.domain.CurrencyFormatter
import com.example.domain.FinancialSummary
import com.example.domain.LocalAppLocalization
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.CustomCashFlowChart
import com.example.ui.components.TransactionCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.VividCoral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    summary: FinancialSummary,
    recentTransactions: List<TransactionEntity>,
    budgets: List<BudgetWithSpending>,
    cashflowPoints: List<CashflowDataPoint> = emptyList(),
    syncState: SyncState,
    onAddTransactionClick: (isIncome: Boolean) -> Unit,
    onSyncClick: () -> Unit,
    onNavigateAnalytics: () -> Unit,
    onNavigateSubscriptions: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    val currentDateStr = SimpleDateFormat("EEEE, MMM d", if (loc.isBurmese()) Locale.ENGLISH else Locale.getDefault()).format(Date())

    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KOREN FINANCE",
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

                // Cloud Sync Status Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(extraColors.cardElevated)
                        .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                        .clickable { onSyncClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("sync_cloud_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (syncState) {
                            is SyncState.Syncing -> {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Syncing",
                                    tint = ElectricEmerald,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(spinRotation)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = loc.t("Syncing...", "ချိတ်ဆက်နေ..."),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            is SyncState.Success -> {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Synced",
                                    tint = ElectricEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = loc.t("Synced", "သိမ်းဆည်းပြီး"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Cloud Sync",
                                    tint = if (summary.pendingSyncCount > 0) AmberGold else extraColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (summary.pendingSyncCount > 0) "${summary.pendingSyncCount} ${loc.t("Pending", "လက်ကျန်")}" else loc.t("Cloud Sync", "Cloud အရန်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (summary.pendingSyncCount > 0) AmberGold else extraColors.textSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hero Total Balance Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (extraColors.isDark) {
                                listOf(
                                    extraColors.cardElevated,
                                    Color(0xFF1B2332),
                                    Color(0xFF142422)
                                )
                            } else {
                                listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFFF1F5F9),
                                    Color(0xFFE2E8F0)
                                )
                            }
                        )
                    )
                    .border(1.dp, extraColors.border, RoundedCornerShape(26.dp))
                    .padding(22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = loc.t("TOTAL BALANCE", "လက်ကျန်ငွေ စုစုပေါင်း"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "MMK • Kyat",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = CurrencyFormatter.formatMMK(summary.totalBalance, includeSymbol = true),
                        style = MaterialTheme.typography.displayMedium,
                        color = extraColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Income & Expense Sub-metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Income Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(extraColors.cardBackground)
                                .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(ElectricEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Income",
                                        tint = ElectricEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = loc.t("Income", "ဝင်ငွေ"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extraColors.textSecondary
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatMMKCompact(summary.monthlyIncome),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = ElectricEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Total Expense Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(extraColors.cardBackground)
                                .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(VividCoral.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Expense",
                                        tint = VividCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = loc.t("Expense", "အသုံးစရိတ်"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extraColors.textSecondary
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatMMKCompact(summary.monthlyExpense),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = VividCoral,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add Expense Button
                Button(
                    onClick = { onAddTransactionClick(false) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_quick_add_expense"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extraColors.cardElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VividCoral.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = VividCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = loc.t("+ Expense", "+ အသုံးစရိတ်"),
                        style = MaterialTheme.typography.labelMedium,
                        color = VividCoral,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Add Income Button
                Button(
                    onClick = { onAddTransactionClick(true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_quick_add_income"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extraColors.cardElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricEmerald.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = loc.t("+ Income", "+ ဝင်ငွေ"),
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Cash Flow Trends Interactive Chart
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                CustomCashFlowChart(dataPoints = cashflowPoints)
            }
        }

        // Active Budgets Warning Alert (if any budget exceeded or warning)
        val warningBudgets = budgets.filter { it.status == BudgetStatus.EXCEEDED || it.status == BudgetStatus.WARNING }
        if (warningBudgets.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (extraColors.isDark) Color(0xFF261A1C) else Color(0xFFFEF2F2))
                        .border(1.dp, VividCoral.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = VividCoral,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = loc.t("Budget Limits Attention", "ဘတ်ဂျက် ကန့်သတ်ချက် သတိပေးချက်"),
                            style = MaterialTheme.typography.titleSmall,
                            color = VividCoral,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = loc.t(
                            "${warningBudgets.size} categories near or exceeding monthly spending targets.",
                            "${warningBudgets.size} ခုသော အမျိုးအစားများ လစဉ်ဘတ်ဂျက် ပြည့်လုနီးပါး သို့မဟုတ် ကျော်လွန်နေပါသည်။"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = extraColors.textSecondary
                    )
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = loc.t("RECENT TRANSACTIONS", "လတ်တလော စာရင်းများ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = loc.t("See Analytics", "အသေးစိတ်ကြည့်ရန်"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateAnalytics() }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Recent Transactions List
        if (recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(extraColors.cardBackground)
                        .border(1.dp, extraColors.border, RoundedCornerShape(18.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = loc.t("No transactions recorded yet", "မှတ်တမ်းစာရင်း မရှိသေးပါ"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = extraColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loc.t("Tap '+' to record your first income or expense", "ပထမဆုံး ဝင်ငွေ/အသုံးစရိတ် ထည့်သွင်းရန် '+' ကိုနှိပ်ပါ"),
                            style = MaterialTheme.typography.labelMedium,
                            color = extraColors.textSecondary
                        )
                    }
                }
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    TransactionCard(
                        transaction = tx,
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }
        }
    }
}
