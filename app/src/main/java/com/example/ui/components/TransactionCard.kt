package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.TransactionEntity
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TransactionCard(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    val isIncome = transaction.type == "INCOME"
    val categoryColor = CategoryIconHelper.parseColor(transaction.categoryColor)
    val localizedCategory = CategoryLocalization.getLocalizedCategoryName(transaction.categoryName, loc.isBurmese())
    val dateFormatted = formatRelativeDate(transaction.timestamp, loc.isBurmese())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(extraColors.cardBackground)
            .border(1.dp, extraColors.border, RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(categoryColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = CategoryIconHelper.getIcon(transaction.categoryIcon),
                    contentDescription = transaction.categoryName,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = extraColors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (transaction.isRecurring) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = "Recurring",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = localizedCategory,
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                    Text(
                        text = " • $dateFormatted",
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textMuted
                    )
                    if (!transaction.walletName.isNullOrBlank()) {
                        Text(
                            text = " • ${transaction.walletName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Amount & Sync Status Column
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = CurrencyFormatter.formatMMK(
                    amount = transaction.amount,
                    includeSymbol = true,
                    withSign = true,
                    isIncome = isIncome
                ),
                style = MaterialTheme.typography.titleMedium,
                color = if (isIncome) ElectricEmerald else extraColors.textPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Sync Status indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (transaction.syncStatus) {
                    "SYNCED" -> {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced to Cloud",
                            tint = extraColors.textMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = loc.t("Synced", "သိမ်းဆည်းပြီး"), style = MaterialTheme.typography.labelSmall, color = extraColors.textMuted)
                    }
                    "PENDING" -> {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Pending Sync",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = loc.t("Local", "ဖုန်းတွင်း"), style = MaterialTheme.typography.labelSmall, color = Color(0xFFFBBF24))
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.SyncProblem,
                            contentDescription = "Sync Error",
                            tint = VividCoral,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = loc.t("Unsynced", "မချိတ်ရသေး"), style = MaterialTheme.typography.labelSmall, color = VividCoral)
                    }
                }
            }
        }
    }
}

private fun formatRelativeDate(timestamp: Long, isBurmese: Boolean): String {
    val cal = Calendar.getInstance()
    val todayStart = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterdayStart = todayStart - 86400000L

    return when {
        timestamp >= todayStart -> if (isBurmese) "ယနေ့" else "Today"
        timestamp >= yesterdayStart -> if (isBurmese) "မနေ့က" else "Yesterday"
        else -> SimpleDateFormat("MMM d", if (isBurmese) Locale.ENGLISH else Locale.getDefault()).format(Date(timestamp))
    }
}
