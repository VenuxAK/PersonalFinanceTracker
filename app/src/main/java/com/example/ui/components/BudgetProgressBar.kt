package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BudgetStatus
import com.example.data.model.BudgetWithSpending
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral

@Composable
fun BudgetProgressBar(
    budget: BudgetWithSpending,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    val animatedProgress by animateFloatAsState(
        targetValue = (budget.percentage / 100f).coerceIn(0f, 1f),
        label = "budget_progress"
    )

    val (statusColor, statusText, statusBg) = when (budget.status) {
        BudgetStatus.SAFE -> Triple(ElectricEmerald, loc.t("Safe", "လုံခြုံ"), ElectricEmerald.copy(alpha = 0.15f))
        BudgetStatus.WARNING -> Triple(AmberGold, loc.t("Near Limit", "ကန့်သတ်နီးပါး"), AmberGold.copy(alpha = 0.15f))
        BudgetStatus.EXCEEDED -> Triple(VividCoral, loc.t("Exceeded", "ကျော်လွန်"), VividCoral.copy(alpha = 0.2f))
    }

    val categoryColor = CategoryIconHelper.parseColor(budget.categoryColor)
    val localizedCategory = CategoryLocalization.getLocalizedCategoryName(budget.categoryName, loc.isBurmese())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(extraColors.cardBackground)
            .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconHelper.getIcon(budget.categoryIcon),
                        contentDescription = budget.categoryName,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = localizedCategory,
                        style = MaterialTheme.typography.titleMedium,
                        color = extraColors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${loc.t("Limit", "ကန့်သတ်")}: ${CurrencyFormatter.formatMMKCompact(budget.monthlyLimit)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                }
            }

            // Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${String.format(java.util.Locale.US, "%.0f%%", budget.percentage)} • $statusText",
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Bar Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(if (extraColors.isDark) Color(0xFF131720) else Color(0xFFE2E8F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = if (budget.status == BudgetStatus.EXCEEDED) {
                                listOf(AmberGold, VividCoral)
                            } else if (budget.status == BudgetStatus.WARNING) {
                                listOf(ElectricEmerald, AmberGold)
                            } else {
                                listOf(ElectricEmerald.copy(alpha = 0.8f), ElectricEmerald)
                            }
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${loc.t("Spent", "သုံးပြီး")}: ${CurrencyFormatter.formatMMK(budget.currentSpent)}",
                style = MaterialTheme.typography.labelSmall,
                color = extraColors.textSecondary
            )
            val remaining = budget.monthlyLimit - budget.currentSpent
            Text(
                text = if (remaining >= 0) {
                    "${loc.t("Left", "ကျန်")}: ${CurrencyFormatter.formatMMKCompact(remaining)}"
                } else {
                    "${loc.t("Over by", "ကျော်လွန်ငွေ")} ${CurrencyFormatter.formatMMKCompact(-remaining)}"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (remaining >= 0) extraColors.textMuted else VividCoral,
                fontWeight = if (remaining < 0) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
