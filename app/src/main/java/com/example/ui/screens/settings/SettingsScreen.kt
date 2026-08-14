package com.example.ui.screens.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncState
import com.example.domain.AppLanguage
import com.example.domain.AppThemeMode
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet

@Composable
fun SettingsScreen(
    syncState: SyncState,
    onSyncClick: () -> Unit,
    onNavigateCategoryManagement: () -> Unit,
    totalTransactions: Int,
    totalCategories: Int,
    totalBudgets: Int,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Screen Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = loc.t("PREFERENCES & CONFIGURATION", "ဆက်တင်နှင့် စိတ်ကြိုက်ပြင်ဆင်မှု"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = loc.t("Settings", "ဆက်တင်"),
                    style = MaterialTheme.typography.displayMedium,
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section: Language Settings
        item {
            SettingsSectionHeader(title = loc.t("LANGUAGE & REGION", "ဘာသာစကားနှင့် ဒေသ"))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // English option
                val isEn = loc.currentLanguage == AppLanguage.ENGLISH
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isEn) extraColors.cardElevated else extraColors.cardBackground)
                        .border(
                            1.5.dp,
                            if (isEn) MaterialTheme.colorScheme.primary else extraColors.border,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { loc.setLanguage(AppLanguage.ENGLISH) }
                        .padding(14.dp)
                        .testTag("setting_lang_en"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🇬🇧", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "English",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isEn) MaterialTheme.colorScheme.primary else extraColors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Default",
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }

                // Burmese option
                val isMy = loc.currentLanguage == AppLanguage.BURMESE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isMy) extraColors.cardElevated else extraColors.cardBackground)
                        .border(
                            1.5.dp,
                            if (isMy) MaterialTheme.colorScheme.primary else extraColors.border,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { loc.setLanguage(AppLanguage.BURMESE) }
                        .padding(14.dp)
                        .testTag("setting_lang_my"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🇲🇲", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "မြန်မာစာ",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isMy) MaterialTheme.colorScheme.primary else extraColors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Burmese (MM)",
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Theme Mode (Dark / Light / System)
        item {
            SettingsSectionHeader(title = loc.t("APPEARANCE & THEME", "အသွင်အပြင်နှင့် သီးမ်"))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(AppThemeMode.DARK, Icons.Default.DarkMode, loc.t("Dark", "အမှောင်")),
                    Triple(AppThemeMode.LIGHT, Icons.Default.LightMode, loc.t("Light", "အလင်း")),
                    Triple(AppThemeMode.SYSTEM, Icons.Default.SettingsBrightness, loc.t("Auto", "စနစ်အတိုင်း"))
                ).forEach { (mode, icon, label) ->
                    val isSelected = loc.currentThemeMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) extraColors.cardElevated else extraColors.cardBackground)
                            .border(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else extraColors.border,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { loc.setThemeMode(mode) }
                            .padding(vertical = 12.dp)
                            .testTag("theme_mode_${mode.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Data & Category Management
        item {
            SettingsSectionHeader(title = loc.t("DATA & CATEGORIES", "ဒေတာနှင့် အမျိုးအစား စီမံခန့်ခွဲမှု"))
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(extraColors.cardBackground)
                    .border(1.dp, extraColors.border, RoundedCornerShape(18.dp))
            ) {
                // Category Management Row
                SettingsNavRow(
                    icon = Icons.Default.Category,
                    iconTint = CyberBlue,
                    title = loc.t("Manage Categories", "အမျိုးအစားများ စီမံမည်"),
                    subtitle = loc.t("$totalCategories income & expense categories", "ဝင်ငွေနှင့် အသုံးစရိတ် $totalCategories မျိုး"),
                    onClick = onNavigateCategoryManagement,
                    testTag = "setting_row_categories"
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(extraColors.border))

                // Cloud Sync Row
                SettingsNavRow(
                    icon = if (syncState is SyncState.Syncing) Icons.Default.Refresh else Icons.Default.CloudSync,
                    iconTint = ElectricEmerald,
                    title = loc.t("Cloud & Local Sync", "Cloud နှင့် ဒေသတွင်း ချိတ်ဆက်မှု"),
                    subtitle = when (syncState) {
                        is SyncState.Syncing -> loc.t("Syncing in progress...", "ဒေတာ ချိတ်ဆက်နေပါသည်...")
                        is SyncState.Success -> loc.t("Synced just now", "ချိတ်ဆက်ပြီးပါပြီ")
                        is SyncState.Error -> loc.t("Operating in offline local-first mode", "ဒေသတွင်း အော့ဖ်လိုင်းစနစ်ဖြင့် အသုံးပြုနေပါသည်")
                        else -> loc.t("Tap to sync with secure backup", "အရန်သိမ်းဆည်းရန် နှိပ်ပါ")
                    },
                    trailingContent = {
                        if (syncState is SyncState.Syncing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = ElectricEmerald)
                        } else {
                            Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = ElectricEmerald, modifier = Modifier.size(20.dp))
                        }
                    },
                    onClick = onSyncClick,
                    testTag = "setting_row_sync"
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Ledger Statistics
        item {
            SettingsSectionHeader(title = loc.t("LEDGER OVERVIEW", "စာရင်းအကျဉ်းချုပ်"))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = loc.t("Transactions", "မှတ်တမ်းပေါင်း"),
                    value = totalTransactions.toString(),
                    color = CyberBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = loc.t("Categories", "အမျိုးအစား"),
                    value = totalCategories.toString(),
                    color = NeonViolet,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = loc.t("Active Budgets", "ဘတ်ဂျက်"),
                    value = totalBudgets.toString(),
                    color = ElectricEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: App Info
        item {
            SettingsSectionHeader(title = loc.t("ABOUT", "အက်ပ်အကြောင်းအရာ"))
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(extraColors.cardBackground)
                    .border(1.dp, extraColors.border, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Koren Finance v2.0 (Bilingual)",
                            style = MaterialTheme.typography.titleMedium,
                            color = extraColors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = loc.t(
                                "Offline-first personal finance with MMK & Multi-language support",
                                "မြန်မာကျပ်ငွေနှင့် အော့ဖ်လိုင်းအသုံးပြုနိုင်သော ငွေကြေးစီမံခန့်ခွဲမှုအက်ပ်"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = extraColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    val extraColors = LocalExtraColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = extraColors.textMuted,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null,
    testTag: String
) {
    val extraColors = LocalExtraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textSecondary
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = extraColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(extraColors.cardBackground)
            .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = extraColors.textSecondary
            )
        }
    }
}
