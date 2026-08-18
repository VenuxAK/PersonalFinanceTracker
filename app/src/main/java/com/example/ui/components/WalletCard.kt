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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletWithBalance
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors

@Composable
fun WalletCard(
    wallet: WalletWithBalance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    val brandColor = CategoryIconHelper.parseColor(wallet.colorHex, fallback = MaterialTheme.colorScheme.primary)

    Box(
        modifier = modifier
            .width(170.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        brandColor.copy(alpha = 0.18f),
                        extraColors.cardElevated
                    )
                )
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) brandColor else extraColors.border,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("wallet_card_${wallet.id}")
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Icon & Default Badge / Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(brandColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getWalletIcon(wallet.iconKey, wallet.type),
                        contentDescription = wallet.name,
                        tint = brandColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (wallet.isDefault) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(brandColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = loc.t("Default", "ပင်မ"),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = brandColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wallet Name & Account Number
            Column {
                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (wallet.accountNumber.isNotBlank()) {
                    Text(
                        text = formatMaskedAccountNumber(wallet.accountNumber),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = extraColors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = when (wallet.type) {
                            "MOBILE_WALLET" -> loc.t("Mobile Pay", "မိုဘိုင်းပိုက်ဆံအိတ်")
                            "BANK_ACCOUNT" -> loc.t("Bank Account", "ဘဏ်အကောင့်")
                            "CASH" -> loc.t("Cash", "လက်ငင်းငွေ")
                            else -> loc.t("Account", "အကောင့်")
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = extraColors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current Balance
            Text(
                text = CurrencyFormatter.formatCompactMMK(wallet.currentBalance, loc.isBurmese()),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (wallet.currentBalance >= 0) extraColors.textPrimary else MaterialTheme.colorScheme.error,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getWalletIcon(iconKey: String, type: String): ImageVector {
    return when {
        iconKey == "payments" || type == "CASH" -> Icons.Default.Payments
        iconKey == "account_balance" || type == "BANK_ACCOUNT" -> Icons.Default.AccountBalance
        else -> Icons.Default.PhoneIphone
    }
}

fun formatMaskedAccountNumber(accountNumber: String): String {
    val clean = accountNumber.trim()
    if (clean.isBlank()) return ""
    // If it's descriptive text rather than an account number, check if digits exist
    val digits = clean.filter { it.isDigit() }
    if (digits.length >= 4) {
        val lastFour = digits.takeLast(4)
        return "•••• $lastFour"
    } else if (clean.startsWith("•") || clean.startsWith("*")) {
        return clean
    } else if (digits.isNotEmpty()) {
        return "•••• $digits"
    }
    return ""
}
