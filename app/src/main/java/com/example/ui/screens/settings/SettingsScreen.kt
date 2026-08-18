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
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SyncState
import com.example.domain.AppLanguage
import com.example.domain.AppThemeMode
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.VividCoral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    syncState: SyncState,
    onSyncClick: () -> Unit,
    onNavigateCategories: () -> Unit,
    onNavigateWallets: () -> Unit = {},
    onResetData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = loc.t("Settings & Preferences", "ဆက်တင်နှင့် စိတ်ကြိုက်ရွေးချယ်မှုများ"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = extraColors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = loc.t("Language, theme, categories & backup", "ဘာသာစကား၊ အရောင်၊ အမျိုးအစားနှင့် အရန်သိမ်းဆည်းမှု"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textSecondary
                )
            }
        }

        // Section: Language Settings
        item {
            SettingsCard {
                SettingsCardHeader(
                    icon = Icons.Default.Language,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = loc.t("App Language", "အသုံးပြုမည့် ဘာသာစကား"),
                    subtitle = loc.t("Switch between English and Burmese", "အင်္ဂလိပ် / မြန်မာ ဘာသာစကား ပြောင်းလဲရန်")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LanguageOption(
                        label = "English (EN)",
                        sublabel = "Default",
                        isSelected = loc.currentLanguage == AppLanguage.ENGLISH,
                        onClick = { loc.setLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f).testTag("btn_lang_en")
                    )

                    LanguageOption(
                        label = "မြန်မာစာ (MM)",
                        sublabel = "Burmese",
                        isSelected = loc.currentLanguage == AppLanguage.BURMESE,
                        onClick = { loc.setLanguage(AppLanguage.BURMESE) },
                        modifier = Modifier.weight(1f).testTag("btn_lang_mm")
                    )
                }
            }
        }

        // Section: Theme Settings
        item {
            SettingsCard {
                SettingsCardHeader(
                    icon = Icons.Default.DarkMode,
                    iconTint = NeonViolet,
                    title = loc.t("Appearance & Theme", "အသွင်အပြင်နှင့် အရောင်"),
                    subtitle = loc.t("Choose dark, light or system style", "မှောင်၊ လင်း သို့မဟုတ် စနစ်အလိုက် ရွေးချယ်ပါ")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOption(
                        icon = Icons.Default.DarkMode,
                        label = loc.t("Dark", "မှောင်"),
                        isSelected = loc.currentThemeMode == AppThemeMode.DARK,
                        onClick = { loc.setThemeMode(AppThemeMode.DARK) },
                        modifier = Modifier.weight(1f).testTag("btn_theme_dark")
                    )

                    ThemeOption(
                        icon = Icons.Default.LightMode,
                        label = loc.t("Light", "လင်း"),
                        isSelected = loc.currentThemeMode == AppThemeMode.LIGHT,
                        onClick = { loc.setThemeMode(AppThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f).testTag("btn_theme_light")
                    )

                    ThemeOption(
                        icon = Icons.Default.SettingsBrightness,
                        label = loc.t("System", "စနစ်"),
                        isSelected = loc.currentThemeMode == AppThemeMode.SYSTEM,
                        onClick = { loc.setThemeMode(AppThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f).testTag("btn_theme_system")
                    )
                }
            }
        }

        // Section: Wallets & Banking Accounts
        item {
            SettingsCard(onClick = onNavigateWallets) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = loc.t("Wallets & Bank Accounts", "ပိုက်ဆံအိတ်နှင့် ဘဏ်များ စီမံရန်"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = extraColors.textPrimary
                            )
                            Text(
                                text = loc.t("KBZPay, CB Pay, Banks & Cash balances", "KBZPay, CB Pay, ဘဏ်များနှင့် လက်ငင်းငွေ စာရင်းများ"),
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = extraColors.textSecondary
                    )
                }
            }
        }

        // Section: Manage Categories Quick Action
        item {
            SettingsCard(onClick = onNavigateCategories) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = loc.t("Category Management", "အမျိုးအစား စီမံခန့်ခွဲမှု"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = extraColors.textPrimary
                            )
                            Text(
                                text = loc.t("Edit names, icons & colors", "အမည်၊ သင်္ကေတနှင့် အရောင်များ စိတ်ကြိုက်ပြင်ဆင်ရန်"),
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = extraColors.textSecondary
                    )
                }
            }
        }

        // Section: Cloud Sync & Backup
        item {
            SettingsCard {
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElectricEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = loc.t("Cloud Sync & Backup", "Cloud အရန်သိမ်းဆည်းမှု"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = extraColors.textPrimary
                            )
                            Text(
                                text = when (syncState) {
                                    is SyncState.Syncing -> loc.t("Syncing records...", "သိမ်းဆည်းနေပါသည်...")
                                    is SyncState.Success -> {
                                        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(syncState.timestamp))
                                        loc.t("Synced at $timeStr (${syncState.count} records)", "$timeStr တွင် အောင်မြင်စွာ သိမ်းဆည်းခဲ့သည်")
                                    }
                                    is SyncState.Error -> loc.t("Offline (Will retry when online)", "အင်တာနက် မရှိပါ (အွန်လိုင်းရောက်လျှင် ပြန်လည်သိမ်းမည်)")
                                    else -> loc.t("Sync data with Cloud Firestore", "Firebase Cloud Firestore ဖြင့် ချိတ်ဆက်သိမ်းဆည်းရန်")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { onSyncClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("btn_settings_sync"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (syncState is SyncState.Syncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = loc.t("Sync Now", "ယခု သိမ်းဆည်းမည်"),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Section: Currency Info
        item {
            SettingsCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = loc.t("Base Currency", "အဓိက ငွေကြေး"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = extraColors.textPrimary
                        )
                        Text(
                            text = "Myanmar Kyat (MMK / ကျပ်)",
                            style = MaterialTheme.typography.bodySmall,
                            color = extraColors.textSecondary
                        )
                    }
                }
            }
        }

        // Section: Reset Data / Danger Zone
        item {
            SettingsCard(onClick = { showResetDialog = true }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(VividCoral.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = VividCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = loc.t("Reset All Data", "ဒေတာ အားလုံး ရှင်းလင်းရန်"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = VividCoral
                            )
                            Text(
                                text = loc.t("Clear all records and restore defaults", "စာရင်းမှတ်တမ်းများကို ဖျက်ပြီး မူလအတိုင်း ပြန်ထားမည်"),
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = VividCoral
                    )
                }
            }
        }

        // Section: About Balancea
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(extraColors.cardBackground)
                    .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_balancea_logo),
                        contentDescription = "Balancea Logo",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Balancea v2.0 • Venux Labs",
                            style = MaterialTheme.typography.titleMedium,
                            color = extraColors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = loc.t("Offline-first bilingual personal finance for Myanmar", "မြန်မာနိုင်ငံအတွက် အော့ဖ်လိုင်းသုံး ငွေကြေးစီမံခန့်ခွဲမှု အက်ပ်"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textSecondary
                        )
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = extraColors.cardBackground,
            title = {
                Text(
                    text = loc.t("Reset All Data?", "ဒေတာ အားလုံး ရှင်းလင်းမည်လား?"),
                    fontWeight = FontWeight.Bold,
                    color = VividCoral
                )
            },
            text = {
                Text(
                    text = loc.t(
                        "Are you sure you want to reset all data? This will permanently delete all transaction records, reset budgets, and restore default Myanmar wallets. This action cannot be undone.",
                        "ဒေတာ အားလုံးကို မူလအတိုင်း ပြန်လည်သတ်မှတ်ရန် သေချာပါသလား? သင်မှတ်သားထားသော စာရင်းမှတ်တမ်းများ အားလုံး ပျက်ပြယ်သွားမည်ဖြစ်ပြီး ပိုက်ဆံအိတ်များနှင့် ဘတ်ဂျက်များကို မူလအနေအထားသို့ ပြန်လည်ထားရှိပါမည်။"
                    ),
                    color = extraColors.textPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividCoral)
                ) {
                    Text(loc.t("Reset Everything", "အားလုံး ရှင်းလင်းမည်"), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(loc.t("Cancel", "ပယ်ဖျက်"), color = extraColors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    onClick: (() -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val extraColors = LocalExtraColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(extraColors.cardBackground)
            .border(1.dp, extraColors.border, RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsCardHeader(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    val extraColors = LocalExtraColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
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
                fontWeight = FontWeight.SemiBold,
                color = extraColors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = extraColors.textSecondary
            )
        }
    }
}

@Composable
private fun LanguageOption(
    label: String,
    sublabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else extraColors.cardElevated)
            .border(
                1.5.dp,
                if (isSelected) primaryColor else extraColors.border,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) primaryColor else extraColors.textPrimary
            )
            Text(
                text = sublabel,
                style = MaterialTheme.typography.labelSmall,
                color = extraColors.textMuted
            )
        }
    }
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else extraColors.cardElevated)
            .border(
                1.5.dp,
                if (isSelected) primaryColor else extraColors.border,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) primaryColor else extraColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) primaryColor else extraColors.textPrimary
            )
        }
    }
}
