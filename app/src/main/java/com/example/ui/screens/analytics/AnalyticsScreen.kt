package com.example.ui.screens.analytics

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.BudgetWithSpending
import com.example.domain.CashflowDataPoint
import com.example.domain.CategoryExpenseBreakdown
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.components.BudgetProgressBar
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.CustomCashFlowChart
import com.example.ui.components.CustomDonutChart
import com.example.ui.components.TransactionCard
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    categoryBreakdowns: List<CategoryExpenseBreakdown>,
    cashflowPoints: List<CashflowDataPoint>,
    budgets: List<BudgetWithSpending>,
    allTransactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    onSetBudget: (categoryId: String, limit: Long) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var selectedTab by remember { mutableStateOf(0) } // 0: Breakdown, 1: Budgets, 2: All History
    var showBudgetDialog by remember { mutableStateOf<BudgetWithSpending?>(null) }
    var showNewBudgetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = loc.t("ANALYTICS & BUDGETS", "စာရင်းသုံးသပ်ချက်နှင့် ဘတ်ဂျက်"),
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = loc.t("Financial Insights", "ငွေကြေးသုံးသပ်ချက်"),
                    style = MaterialTheme.typography.displayMedium,
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Segmented Tab Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    loc.t("Spending Chart", "အသုံးစရိတ်ဇယား"),
                    loc.t("Budgets", "ဘတ်ဂျက်"),
                    loc.t("History", "စာရင်းမှတ်တမ်း")
                )
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) extraColors.cardElevated else extraColors.cardBackground)
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else extraColors.border,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp)
                            .testTag("analytics_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tab 0: Charts & Breakdown
        if (selectedTab == 0) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = loc.t("EXPENSE BY CATEGORY", "အမျိုးအစားအလိုက် အသုံးစရိတ်"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomDonutChart(breakdowns = categoryBreakdowns)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    CustomCashFlowChart(dataPoints = cashflowPoints)
                }
            }
        }

        // Tab 1: Budgets
        if (selectedTab == 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = loc.t("SPENDING LIMITS", "လစဉ်ဘတ်ဂျက် ကန့်သတ်ချက်များ"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricEmerald.copy(alpha = 0.15f))
                            .clickable { showNewBudgetDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("btn_set_budget_dialog")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Budget",
                                tint = ElectricEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = loc.t("Set Budget", "ဘတ်ဂျက်သတ်မှတ်ရန်"),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (budgets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(extraColors.cardBackground)
                            .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = loc.t("No budgets configured yet. Tap 'Set Budget' above.", "ဘတ်ဂျက် မသတ်မှတ်ရသေးပါ။ 'ဘတ်ဂျက်သတ်မှတ်ရန်' ကိုနှိပ်ပါ။"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = extraColors.textSecondary
                        )
                    }
                }
            } else {
                items(budgets, key = { it.categoryId }) { budget ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        BudgetProgressBar(
                            budget = budget,
                            onClick = { showBudgetDialog = budget }
                        )
                    }
                }
            }
        }

        // Tab 2: All Transaction History
        if (selectedTab == 2) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = loc.t("ALL TRANSACTIONS (${allTransactions.size})", "မှတ်တမ်းအားလုံး (${allTransactions.size})"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (allTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(extraColors.cardBackground)
                            .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = loc.t("No transaction history available", "မှတ်တမ်း မရှိသေးပါ"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = extraColors.textSecondary
                        )
                    }
                }
            } else {
                items(allTransactions, key = { it.id }) { tx ->
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

    // Edit Existing Budget Dialog
    if (showBudgetDialog != null) {
        val target = showBudgetDialog!!
        var limitInput by remember { mutableStateOf(target.monthlyLimit.toString()) }
        val localizedCat = CategoryLocalization.getLocalizedCategoryName(target.categoryName, loc.isBurmese())

        AlertDialog(
            onDismissRequest = { showBudgetDialog = null },
            title = {
                Text(
                    text = "${loc.t("Budget for", "ဘတ်ဂျက်:")} $localizedCat",
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = loc.t("Set monthly spending target:", "လစဉ် အသုံးစရိတ် ကန့်သတ်ချက် ထည့်ပါ:"),
                        color = extraColors.textSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) limitInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(loc.t("Limit (MMK)", "ကန့်သတ်ငွေ (ကျပ်)")) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extraColors.textPrimary,
                            unfocusedTextColor = extraColors.textPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = extraColors.border
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLimit = limitInput.toLongOrNull() ?: target.monthlyLimit
                        onSetBudget(target.categoryId, newLimit)
                        showBudgetDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(loc.t("Update Limit", "ပြင်ဆင်မည်"), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = null }) {
                    Text(loc.t("Cancel", "မလုပ်တော့ပါ"), color = extraColors.textSecondary)
                }
            },
            containerColor = extraColors.cardBackground
        )
    }

    // Set New Budget Dialog
    if (showNewBudgetDialog) {
        val expenseCats = categories.filter { it.type == "EXPENSE" }
        var selectedCatId by remember { mutableStateOf(expenseCats.firstOrNull()?.id ?: "") }
        var limitInput by remember { mutableStateOf("300000") }

        AlertDialog(
            onDismissRequest = { showNewBudgetDialog = false },
            title = {
                Text(
                    text = loc.t("Set Monthly Budget", "လစဉ်ဘတ်ဂျက် သတ်မှတ်ရန်"),
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = loc.t("Choose category & budget target:", "အမျိုးအစားနှင့် ငွေပမာဏ ရွေးချယ်ပါ:"),
                        color = extraColors.textSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.height(140.dp)) {
                        items(expenseCats) { cat ->
                            val isSel = cat.id == selectedCatId
                            val localizedCat = CategoryLocalization.getLocalizedCategoryName(cat.name, loc.isBurmese())
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { selectedCatId = cat.id }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(CategoryIconHelper.parseColor(cat.colorHex))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = localizedCat,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else extraColors.textPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) limitInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(loc.t("Limit (MMK)", "ကန့်သတ်ငွေ (ကျပ်)")) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extraColors.textPrimary,
                            unfocusedTextColor = extraColors.textPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = extraColors.border
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = limitInput.toLongOrNull() ?: 0L
                        if (selectedCatId.isNotBlank() && limit > 0) {
                            onSetBudget(selectedCatId, limit)
                        }
                        showNewBudgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(loc.t("Save Budget", "သိမ်းဆည်းမည်"), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewBudgetDialog = false }) {
                    Text(loc.t("Cancel", "မလုပ်တော့ပါ"), color = extraColors.textSecondary)
                }
            },
            containerColor = extraColors.cardBackground
        )
    }
}
