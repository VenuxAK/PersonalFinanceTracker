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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.BudgetWithSpending
import com.example.domain.CategoryExpenseBreakdown
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.FinancialSummary
import com.example.domain.LocalAppLocalization
import com.example.ui.components.BudgetProgressBar
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.CustomDonutChart
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral

@Composable
fun AnalyticsScreen(
    summary: FinancialSummary,
    categoryBreakdown: List<CategoryExpenseBreakdown>,
    budgets: List<BudgetWithSpending>,
    allCategories: List<CategoryEntity>,
    onSetBudget: (categoryId: String, limit: Long) -> Unit,
    onDeleteBudget: (categoryId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var showBudgetDialog by remember { mutableStateOf(false) }
    var selectedBudgetCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var limitInput by remember { mutableStateOf("") }

    val expenseCategories = allCategories.filter { it.type == "EXPENSE" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loc.t("Expense Analytics", "အသုံးစရိတ် သုံးသပ်ချက်"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = loc.t("Spending breakdown & budget tracking", "အသုံးစရိတ် ခွဲခြမ်းစိတ်ဖြာမှု နှင့် ကန့်သတ်ချက်များ"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .clickable {
                            selectedBudgetCategory = expenseCategories.firstOrNull()
                            limitInput = ""
                            showBudgetDialog = true
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("btn_add_budget"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = loc.t("Set Budget", "ကန့်သတ်ရန်"),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Donut Chart & Category Spending Section
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
                    Text(
                        text = loc.t("Spending by Category", "အမျိုးအစားအလိုက် အသုံးစရိတ်"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (categoryBreakdown.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loc.t("No expenses recorded yet", "အသုံးစရိတ် မှတ်တမ်း မရှိသေးပါ"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = extraColors.textMuted
                            )
                        }
                    } else {
                        CustomDonutChart(
                            breakdowns = categoryBreakdown,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Category List with percentages
                        categoryBreakdown.forEach { item ->
                            val catColor = CategoryIconHelper.parseColor(item.colorHex)
                            val catName = CategoryLocalization.getLocalizedCategoryName(item.categoryName, loc.isBurmese())

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(catColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = CategoryIconHelper.getIcon(item.iconKey),
                                            contentDescription = null,
                                            tint = catColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = catName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = extraColors.textPrimary
                                        )
                                        Text(
                                            text = "${String.format(java.util.Locale.US, "%.1f", item.percentage)}% ${loc.t("of expenses", "အသုံးစရိတ်၏")}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extraColors.textSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = CurrencyFormatter.formatMMK(item.totalAmount, loc.isBurmese()),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = extraColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Monthly Budgets Header
        item {
            Text(
                text = loc.t("Category Budgets & Limits", "လစဉ် ကန့်သတ်ငွေ သတ်မှတ်ချက်များ"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = extraColors.textPrimary
            )
        }

        if (budgets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(extraColors.cardBackground)
                        .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = loc.t("No monthly budgets set", "လစဉ်ကန့်သတ်ငွေ မသတ်မှတ်ရသေးပါ"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = extraColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loc.t("Tap '+ Set Budget' above to prevent overspending", "အသုံးမလွန်စေရန် အပေါ်ရှိ '+ ကန့်သတ်ရန်' ကိုနှိပ်ပါ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }
            }
        } else {
            items(budgets, key = { it.categoryId }) { budget ->
                BudgetProgressBar(
                    budget = budget,
                    onEdit = {
                        selectedBudgetCategory = allCategories.find { it.id == budget.categoryId }
                        limitInput = budget.monthlyLimit.toString()
                        showBudgetDialog = true
                    },
                    onDelete = { onDeleteBudget(budget.categoryId) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Set Budget Dialog
    if (showBudgetDialog && selectedBudgetCategory != null) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            containerColor = extraColors.cardBackground,
            title = {
                Text(
                    text = loc.t("Set Category Budget", "ကန့်သတ်ငွေ သတ်မှတ်ရန်"),
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = loc.t("Select Category", "အမျိုးအစား ရွေးချယ်ပါ"),
                        style = MaterialTheme.typography.labelMedium,
                        color = extraColors.textSecondary
                    )

                    // Category chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        expenseCategories.take(4).forEach { cat ->
                            val isSel = cat.id == selectedBudgetCategory?.id
                            val catColor = CategoryIconHelper.parseColor(cat.colorHex)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) catColor.copy(alpha = 0.2f) else extraColors.cardElevated)
                                    .border(1.dp, if (isSel) catColor else extraColors.border, RoundedCornerShape(8.dp))
                                    .clickable { selectedBudgetCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = CategoryLocalization.getLocalizedCategoryName(cat.name, loc.isBurmese()),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSel) extraColors.textPrimary else extraColors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = loc.t("Monthly Limit (MMK)", "လစဉ် ကန့်သတ်ပမာဏ (ကျပ်)"),
                        style = MaterialTheme.typography.labelMedium,
                        color = extraColors.textSecondary
                    )

                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) limitInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("e.g. 150000 Ks", color = extraColors.textMuted) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = extraColors.textPrimary,
                            fontWeight = FontWeight.Medium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extraColors.textPrimary,
                            unfocusedTextColor = extraColors.textPrimary,
                            focusedContainerColor = extraColors.cardElevated,
                            unfocusedContainerColor = extraColors.cardElevated,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = extraColors.border,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = limitInput.toLongOrNull() ?: 0L
                        if (limit > 0 && selectedBudgetCategory != null) {
                            onSetBudget(selectedBudgetCategory!!.id, limit)
                            showBudgetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(loc.t("Save Limit", "သတ်မှတ်မည်"), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text(loc.t("Cancel", "ပယ်ဖျက်"), color = extraColors.textSecondary)
                }
            }
        )
    }
}
