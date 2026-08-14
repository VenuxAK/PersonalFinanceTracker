package com.example.ui.screens.recurring

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.VividCoral

@Composable
fun RecurringScreen(
    recurringTransactions: List<TransactionEntity>,
    onApplyRecurring: (TransactionEntity) -> Unit,
    onAddRecurringClick: () -> Unit,
    onEditRecurring: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    val totalMonthlySubscriptionExpenses = recurringTransactions
        .filter { it.type == "EXPENSE" }
        .sumOf { tx ->
            when (tx.frequency) {
                "DAILY" -> tx.amount * 30
                "WEEKLY" -> tx.amount * 4
                "YEARLY" -> tx.amount / 12
                else -> tx.amount
            }
        }

    val totalMonthlyRecurringIncome = recurringTransactions
        .filter { it.type == "INCOME" }
        .sumOf { tx ->
            when (tx.frequency) {
                "DAILY" -> tx.amount * 30
                "WEEKLY" -> tx.amount * 4
                "YEARLY" -> tx.amount / 12
                else -> tx.amount
            }
        }

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
                    text = loc.t("RECURRING & SUBSCRIPTIONS", "ပုံမှန်ပေးသွင်းစာရင်းများ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonViolet,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = loc.t("Scheduled Dues", "ပုံမှန်ပေးသွင်းစာရင်း"),
                    style = MaterialTheme.typography.displayMedium,
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Summary Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (extraColors.isDark) {
                                listOf(
                                    extraColors.cardElevated,
                                    Color(0xFF211832),
                                    Color(0xFF161922)
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
                    .border(1.dp, extraColors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = loc.t("MONTHLY OBLIGATION", "လစဉ်ပေးသွင်းရန် ခန့်မှန်းခြေ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = null,
                            tint = NeonViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = CurrencyFormatter.formatMMK(totalMonthlySubscriptionExpenses, includeSymbol = true),
                        style = MaterialTheme.typography.headlineLarge,
                        color = VividCoral,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = loc.t("Monthly Scheduled Income", "လစဉ် ပုံမှန်ဝင်ငွေ"),
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                            Text(
                                text = "+${CurrencyFormatter.formatMMKCompact(totalMonthlyRecurringIncome)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = loc.t("Active Plans", "အစီအစဉ် အရေအတွက်"),
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                            Text(
                                text = loc.t("${recurringTransactions.size} Items", "${recurringTransactions.size} ခု"),
                                style = MaterialTheme.typography.titleMedium,
                                color = extraColors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Add Recurring Action Row
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = loc.t("ACTIVE RECURRING ITEMS", "လက်ရှိ ပုံမှန်စာရင်းများ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonViolet.copy(alpha = 0.15f))
                        .clickable { onAddRecurringClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_add_recurring")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Recurring",
                            tint = NeonViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = loc.t("New Recurring", "စာရင်းအသစ်"),
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonViolet,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // List of Recurring items
        if (recurringTransactions.isEmpty()) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = loc.t("No recurring plans or subscriptions active", "ပုံမှန်ပေးသွင်းစာရင်း မရှိသေးပါ"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = extraColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loc.t("Toggle 'Make Recurring' when adding records to track dues", "စာရင်းထည့်သွင်းရာတွင် ပုံမှန်ပေးသွင်းစာရင်း အဖြစ်သတ်မှတ်ပါ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }
            }
        } else {
            items(recurringTransactions, key = { it.id }) { item ->
                val isIncome = item.type == "INCOME"
                val catColor = CategoryIconHelper.parseColor(item.categoryColor)
                val localizedCat = CategoryLocalization.getLocalizedCategoryName(item.categoryName, loc.isBurmese())
                val frequencyLabel = when (item.frequency) {
                    "DAILY" -> loc.t("Daily", "နေ့စဉ်")
                    "WEEKLY" -> loc.t("Weekly", "အပတ်စဉ်")
                    "MONTHLY" -> loc.t("Monthly", "လစဉ်")
                    "YEARLY" -> loc.t("Yearly", "နှစ်စဉ်")
                    else -> item.frequency
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(extraColors.cardBackground)
                        .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
                        .clickable { onEditRecurring(item) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(catColor.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = CategoryIconHelper.getIcon(item.categoryIcon),
                                        contentDescription = item.categoryName,
                                        tint = catColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = extraColors.textPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = localizedCat,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extraColors.textSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isIncome) "+" else "-"}${CurrencyFormatter.formatMMK(item.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isIncome) ElectricEmerald else VividCoral,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonViolet.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = frequencyLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonViolet,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick action bar: Apply payment record instance now
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = extraColors.textMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = loc.t("Cycle active", "ပုံမှန်လည်ပတ်နေသည်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textMuted
                                )
                            }

                            Button(
                                onClick = { onApplyRecurring(item) },
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("btn_pay_recurring_${item.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isIncome) ElectricEmerald.copy(alpha = 0.2f) else VividCoral.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = if (isIncome) ElectricEmerald else VividCoral,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isIncome) loc.t("Post Income Now", "ဝင်ငွေထည့်မည်") else loc.t("Post Expense Now", "အသုံးစရိတ်ထည့်မည်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isIncome) ElectricEmerald else VividCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
