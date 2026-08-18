package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.WalletEntity
import com.example.data.model.WalletPreset
import com.example.data.model.WalletWithBalance
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.VividCoral

object MyanmarWalletPresets {
    val presets = listOf(
        WalletPreset("preset_kbzpay", "KBZPay", "MOBILE_WALLET", "#0066B2", "phone_iphone", "ကေဘီဇက်ပလက်စတစ်"),
        WalletPreset("preset_cbpay", "CB Pay", "MOBILE_WALLET", "#E65100", "phone_iphone", "စီဘီပလက်စတစ်"),
        WalletPreset("preset_uabpay", "UABpay", "MOBILE_WALLET", "#6A1B9A", "phone_iphone", "ယူအေဘီပလက်စတစ်"),
        WalletPreset("preset_yomapay", "YOMA Pay / Next", "MOBILE_WALLET", "#F57C00", "phone_iphone", "ရိုးမပလက်စတစ်"),
        WalletPreset("preset_ayapay", "AYA Pay", "MOBILE_WALLET", "#D32F2F", "phone_iphone", "ဧရာဝတီပလက်စတစ်"),
        WalletPreset("preset_wavepay", "Wave Money", "MOBILE_WALLET", "#FBBF24", "phone_iphone", "ဝေ့ဖ်မန်းနီး"),
        WalletPreset("preset_cash", "Cash (လက်ငင်းငွေ)", "CASH", "#10B981", "payments", "လက်ကျန် ငွေသား"),
        WalletPreset("preset_kbzbank", "KBZ Bank", "BANK_ACCOUNT", "#004C97", "account_balance", "ကေဘီဇက်ဘဏ်"),
        WalletPreset("preset_cbbank", "CB Bank", "BANK_ACCOUNT", "#005BAA", "account_balance", "စီဘီဘဏ်"),
        WalletPreset("preset_ayabank", "AYA Bank", "BANK_ACCOUNT", "#C62828", "account_balance", "ဧရာဝတီဘဏ်"),
        WalletPreset("preset_yomabank", "YOMA Bank", "BANK_ACCOUNT", "#EF6C00", "account_balance", "ရိုးမဘဏ်"),
        WalletPreset("preset_uabbank", "UAB Bank", "BANK_ACCOUNT", "#4A148C", "account_balance", "ယူအေဘီဘဏ်"),
        WalletPreset("preset_agdbank", "AGD Bank", "BANK_ACCOUNT", "#2E7D32", "account_balance", "အေဂျီဒီဘဏ်"),
        WalletPreset("preset_mabbank", "MAB Bank", "BANK_ACCOUNT", "#1565C0", "account_balance", "မြန်မာ့ရှေ့ဆောင်ဘဏ်"),
        WalletPreset("preset_custom", "Custom Bank / Wallet", "OTHER", "#64748B", "account_balance_wallet", "အခြားဘဏ် / ပိုက်ဆံအိတ်")
    )

    val colors = listOf(
        "#0066B2", "#004C97", "#E65100", "#005BAA",
        "#D32F2F", "#C62828", "#6A1B9A", "#4A148C",
        "#F57C00", "#EF6C00", "#FBBF24", "#10B981",
        "#06B6D4", "#6366F1", "#EC4899", "#64748B"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletManagementSheet(
    wallets: List<WalletWithBalance>,
    onDismiss: () -> Unit,
    onAddWallet: (name: String, type: String, initialBalance: Long, colorHex: String, iconKey: String, accountNumber: String, isDefault: Boolean) -> Unit,
    onUpdateWallet: (WalletEntity) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onTransfer: (fromId: String, fromName: String, toId: String, toName: String, amount: Long, note: String) -> Unit,
    onAdjustBalance: (walletId: String, newBalance: Long) -> Unit = { _, _ -> }
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var selectedWalletForDetail by remember { mutableStateOf<WalletWithBalance?>(null) }
    var walletToEdit by remember { mutableStateOf<WalletWithBalance?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = extraColors.cardBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loc.t("Wallets & Bank Accounts", "ပိုက်ဆံအိတ်နှင့် ဘဏ်အကောင့်များ"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.textPrimary
                    )
                    Text(
                        text = loc.t("Manage KBZPay, CB Pay, Banks & Cash", "KBZPay, CB Pay, ဘဏ်များနှင့် လက်ငင်းငွေ စီမံရန်"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = extraColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Add Wallet & Transfer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f).testTag("btn_add_wallet_sheet"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = loc.t("Add Wallet", "အကောင့်သစ်"),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (wallets.size >= 2) {
                    OutlinedButton(
                        onClick = { showTransferDialog = true },
                        modifier = Modifier.weight(1f).testTag("btn_transfer_funds_sheet"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, extraColors.border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = extraColors.cardElevated,
                            contentColor = extraColors.textPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = extraColors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Transfer", "ငွေလွှဲမည်"),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = extraColors.textPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of Wallets
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wallets, key = { it.id }) { wallet ->
                    val brandColor = CategoryIconHelper.parseColor(wallet.colorHex, fallback = MaterialTheme.colorScheme.primary)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(extraColors.cardElevated)
                            .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                            .clickable { selectedWalletForDetail = wallet }
                            .padding(14.dp)
                            .testTag("manage_wallet_row_${wallet.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(brandColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getWalletIcon(wallet.iconKey, wallet.type),
                                        contentDescription = wallet.name,
                                        tint = brandColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = wallet.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = extraColors.textPrimary
                                        )
                                        if (wallet.isDefault) {
                                             Spacer(modifier = Modifier.width(6.dp))
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

                                    if (wallet.accountNumber.isNotBlank()) {
                                        Text(
                                            text = formatMaskedAccountNumber(wallet.accountNumber),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extraColors.textMuted
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CurrencyFormatter.formatMMK(wallet.currentBalance, loc.isBurmese()),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (wallet.currentBalance >= 0) extraColors.textPrimary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "${wallet.transactionCount} ${loc.t("records", "မှတ်တမ်း")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Wallet Dialog
    if (showAddDialog || walletToEdit != null) {
        AddEditWalletDialog(
            initialWallet = walletToEdit,
            onDismiss = {
                showAddDialog = false
                walletToEdit = null
            },
            onSave = { name, type, bal, color, icon, accNum, isDef ->
                if (walletToEdit != null) {
                    val currentToEdit = walletToEdit!!
                    val updatedEntity = WalletEntity(
                        id = currentToEdit.id,
                        name = name,
                        type = type,
                        initialBalance = currentToEdit.initialBalance,
                        colorHex = color,
                        iconKey = icon,
                        accountNumber = accNum,
                        isDefault = isDef,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    onUpdateWallet(updatedEntity)
                    // If balance was adjusted
                    if (bal != currentToEdit.currentBalance) {
                        onAdjustBalance(currentToEdit.id, bal)
                    }
                } else {
                    onAddWallet(name, type, bal, color, icon, accNum, isDef)
                }
                showAddDialog = false
                walletToEdit = null
            }
        )
    }

    // Transfer Funds Dialog
    if (showTransferDialog) {
        TransferFundsDialog(
            wallets = wallets,
            onDismiss = { showTransferDialog = false },
            onTransfer = { fromId, fromName, toId, toName, amount, note ->
                onTransfer(fromId, fromName, toId, toName, amount, note)
                showTransferDialog = false
            }
        )
    }

    // Wallet Detail Dialog
    selectedWalletForDetail?.let { detailWallet ->
        WalletDetailDialog(
            wallet = detailWallet,
            onDismiss = { selectedWalletForDetail = null },
            onEdit = {
                walletToEdit = detailWallet
                selectedWalletForDetail = null
            },
            onDelete = {
                onDeleteWallet(detailWallet.id)
                selectedWalletForDetail = null
            },
            onAdjustBalance = { newBal ->
                onAdjustBalance(detailWallet.id, newBal)
                selectedWalletForDetail = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditWalletDialog(
    initialWallet: WalletWithBalance?,
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, balance: Long, colorHex: String, iconKey: String, accountNumber: String, isDefault: Boolean) -> Unit
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var selectedPreset by remember {
        mutableStateOf(
            MyanmarWalletPresets.presets.firstOrNull { it.name == initialWallet?.name }
        )
    }
    var name by remember { mutableStateOf(initialWallet?.name ?: "") }
    var type by remember { mutableStateOf(initialWallet?.type ?: "MOBILE_WALLET") }
    var balanceText by remember { mutableStateOf(initialWallet?.let { it.currentBalance.toString() } ?: "") }
    var colorHex by remember { mutableStateOf(initialWallet?.colorHex ?: "#0066B2") }
    var iconKey by remember { mutableStateOf(initialWallet?.iconKey ?: "phone_iphone") }
    var accountNumber by remember { mutableStateOf(initialWallet?.accountNumber ?: "") }
    var isDefault by remember { mutableStateOf(initialWallet?.isDefault ?: false) }

    val unfocusedBorderColor = if (extraColors.isDark) extraColors.border else Color(0xFF94A3B8)
    val unfocusedLabelColor = if (extraColors.isDark) extraColors.textSecondary else Color(0xFF475569)
    val placeholderColor = if (extraColors.isDark) extraColors.textMuted else Color(0xFF64748B)
    val fieldContainerColor = if (extraColors.isDark) extraColors.cardElevated else Color(0xFFF8FAFC)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = extraColors.cardBackground,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = if (initialWallet == null) loc.t("Add Wallet / Bank", "ပိုက်ဆံအိတ် / ဘဏ်အကောင့် ထည့်ရန်") else loc.t("Edit Wallet", "ပိုက်ဆံအိတ် ပြင်ဆင်ရန်"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.textPrimary
                    )
                }

                // Quick Myanmar Presets selector
                item {
                    Column {
                        Text(
                            text = loc.t("Select Bank / Payment Service", "မြန်မာငွေပေးချေမှု / ဘဏ် ရွေးချယ်ပါ"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = extraColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MyanmarWalletPresets.presets.forEach { preset ->
                                val isSelected = preset.name == name
                                val presetColor = CategoryIconHelper.parseColor(preset.colorHex)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) presetColor.copy(alpha = 0.2f) else extraColors.cardElevated)
                                        .border(1.dp, if (isSelected) presetColor else unfocusedBorderColor, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedPreset = preset
                                            name = preset.name
                                            type = preset.type
                                            colorHex = preset.colorHex
                                            iconKey = preset.iconKey
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = preset.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) presetColor else extraColors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Name Input
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(loc.t("Account / Wallet Name", "အကောင့် / ပိုက်ဆံအိတ် အမည်")) },
                        placeholder = { 
                            Text(
                                "KBZPay, CB Pay, Cash, AYA Bank...",
                                color = placeholderColor
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_wallet_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extraColors.textPrimary,
                            unfocusedTextColor = extraColors.textPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = unfocusedBorderColor,
                            focusedContainerColor = fieldContainerColor,
                            unfocusedContainerColor = fieldContainerColor,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = unfocusedLabelColor,
                            focusedPlaceholderColor = placeholderColor,
                            unfocusedPlaceholderColor = placeholderColor,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Balance Input (Adjust Current Balance or Set Starting Balance)
                item {
                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = { balanceText = it.filter { ch -> ch.isDigit() } },
                        label = { 
                            Text(
                                if (initialWallet == null) 
                                    loc.t("Starting Balance (MMK / ကျပ်)", "စတင် လက်ကျန်ငွေ") 
                                else 
                                    loc.t("Current Balance (MMK / ကျပ်)", "လက်ရှိ လက်ကျန်ငွေ")
                            ) 
                        },
                        placeholder = {
                            Text(
                                "e.g. 500000",
                                color = placeholderColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_wallet_balance"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extraColors.textPrimary,
                            unfocusedTextColor = extraColors.textPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = unfocusedBorderColor,
                            focusedContainerColor = fieldContainerColor,
                            unfocusedContainerColor = fieldContainerColor,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = unfocusedLabelColor,
                            focusedPlaceholderColor = placeholderColor,
                            unfocusedPlaceholderColor = placeholderColor,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Phone / Account Number Input
                item {
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = { Text(loc.t("Account / Phone Number (Optional)", "ဖုန်းနံပါတ် / အကောင့်နံပါတ် (စိတ်ကြိုက်)")) },
                        placeholder = { 
                            Text(
                                "09xxxxxxxxx or 123456789",
                                color = placeholderColor
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_wallet_acc_num"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extraColors.textPrimary,
                            unfocusedTextColor = extraColors.textPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = unfocusedBorderColor,
                            focusedContainerColor = fieldContainerColor,
                            unfocusedContainerColor = fieldContainerColor,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = unfocusedLabelColor,
                            focusedPlaceholderColor = placeholderColor,
                            unfocusedPlaceholderColor = placeholderColor,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Color Picker with smooth horizontal scroll
                item {
                    Column {
                        Text(
                            text = loc.t("Theme Color (Swipe to choose)", "အရောင် ရွေးချယ်ပါ (ဘေးသို့ ရွှေ့ပါ)"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = extraColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(MyanmarWalletPresets.colors) { hex ->
                                val color = CategoryIconHelper.parseColor(hex)
                                val isSelected = colorHex.equals(hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(2.5.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                                        .clickable { colorHex = hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Buttons: Cancel & Save
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = loc.t("Cancel", "ပယ်ဖျက်"),
                                color = unfocusedLabelColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val bal = balanceText.toLongOrNull() ?: 0L
                                    onSave(name.trim(), type, bal, colorHex, iconKey, accountNumber.trim(), isDefault)
                                }
                            },
                            enabled = name.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                disabledContainerColor = if (extraColors.isDark) Color(0xFF262B38) else Color(0xFFE2E8F0),
                                disabledContentColor = if (extraColors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("btn_save_wallet")
                        ) {
                            Text(
                                text = loc.t("Save Wallet", "သိမ်းဆည်းမည်"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransferFundsDialog(
    wallets: List<WalletWithBalance>,
    onDismiss: () -> Unit,
    onTransfer: (fromId: String, fromName: String, toId: String, toName: String, amount: Long, note: String) -> Unit
) {
    if (wallets.isEmpty()) {
        return
    }
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    val unfocusedBorderColor = if (extraColors.isDark) extraColors.border else Color(0xFF94A3B8)
    val unfocusedLabelColor = if (extraColors.isDark) extraColors.textSecondary else Color(0xFF475569)
    val placeholderColor = if (extraColors.isDark) extraColors.textMuted else Color(0xFF64748B)
    val fieldContainerColor = if (extraColors.isDark) extraColors.cardElevated else Color(0xFFF8FAFC)

    var fromWallet by remember { mutableStateOf(wallets.first()) }
    var toWallet by remember { mutableStateOf(wallets.getOrNull(1) ?: wallets.first()) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extraColors.cardBackground,
        title = {
            Text(
                text = loc.t("Transfer Funds", "ပိုက်ဆံအိတ် အချင်းချင်း ငွေလွှဲရန်"),
                fontWeight = FontWeight.Bold,
                color = extraColors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = loc.t("Move money between your KBZPay, CB Pay, Cash, and Banks effortlessly.", "မိမိ၏ ပိုက်ဆံအိတ်များနှင့် ဘဏ်များအကြား လွယ်ကူစွာ ငွေလွှဲပြောင်းနိုင်ပါသည်။"),
                    style = MaterialTheme.typography.bodySmall,
                    color = extraColors.textSecondary
                )

                // From Wallet Selector
                Column {
                    Text(
                        text = loc.t("From (မှ)", "ငွေထုတ်မည့် ပိုက်ဆံအိတ်"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(wallets) { w ->
                            val isSel = w.id == fromWallet.id
                            val brandColor = CategoryIconHelper.parseColor(w.colorHex)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) brandColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { fromWallet = w }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(w.name, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = extraColors.textPrimary)
                                Text(CurrencyFormatter.formatCompactMMK(w.currentBalance, loc.isBurmese()), style = MaterialTheme.typography.labelSmall, color = extraColors.textSecondary)
                            }
                        }
                    }
                }

                // To Wallet Selector
                Column {
                    Text(
                        text = loc.t("To (သို့)", "ငွေလက်ခံမည့် ပိုက်ဆံအိတ်"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(wallets) { w ->
                            val isSel = w.id == toWallet.id
                            val brandColor = CategoryIconHelper.parseColor(w.colorHex)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) brandColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { toWallet = w }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(w.name, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = extraColors.textPrimary)
                                Text(CurrencyFormatter.formatCompactMMK(w.currentBalance, loc.isBurmese()), style = MaterialTheme.typography.labelSmall, color = extraColors.textSecondary)
                            }
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(loc.t("Transfer Amount (MMK)", "လွှဲပြောင်းမည့် ပမာဏ")) },
                    placeholder = { Text("e.g. 50000", color = placeholderColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_transfer_amount"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = extraColors.textPrimary,
                        unfocusedTextColor = extraColors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = unfocusedBorderColor,
                        focusedContainerColor = fieldContainerColor,
                        unfocusedContainerColor = fieldContainerColor,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = unfocusedLabelColor,
                        focusedPlaceholderColor = placeholderColor,
                        unfocusedPlaceholderColor = placeholderColor,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(loc.t("Note (Optional)", "မှတ်ချက်")) },
                    placeholder = { Text("e.g. Top up KBZPay", color = placeholderColor) },
                    modifier = Modifier.fillMaxWidth().testTag("input_transfer_note"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = extraColors.textPrimary,
                        unfocusedTextColor = extraColors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = unfocusedBorderColor,
                        focusedContainerColor = fieldContainerColor,
                        unfocusedContainerColor = fieldContainerColor,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = unfocusedLabelColor,
                        focusedPlaceholderColor = placeholderColor,
                        unfocusedPlaceholderColor = placeholderColor,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    if (amount > 0 && fromWallet.id != toWallet.id) {
                        onTransfer(fromWallet.id, fromWallet.name, toWallet.id, toWallet.name, amount, note)
                    }
                },
                enabled = (amountText.toLongOrNull() ?: 0L) > 0 && fromWallet.id != toWallet.id,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContainerColor = if (extraColors.isDark) Color(0xFF262B38) else Color(0xFFE2E8F0),
                    disabledContentColor = if (extraColors.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                ),
                modifier = Modifier.testTag("btn_confirm_transfer")
            ) {
                Text(loc.t("Transfer Now", "ငွေလွှဲမည်"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(loc.t("Cancel", "ပယ်ဖျက်"), color = unfocusedLabelColor, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun AdjustBalanceDialog(
    walletName: String,
    currentBalance: Long,
    onDismiss: () -> Unit,
    onConfirm: (newBalance: Long) -> Unit
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    val unfocusedBorderColor = if (extraColors.isDark) extraColors.border else Color(0xFF94A3B8)
    val unfocusedLabelColor = if (extraColors.isDark) extraColors.textSecondary else Color(0xFF475569)
    val placeholderColor = if (extraColors.isDark) extraColors.textMuted else Color(0xFF64748B)
    val fieldContainerColor = if (extraColors.isDark) extraColors.cardElevated else Color(0xFFF8FAFC)

    var newBalanceText by remember { mutableStateOf(currentBalance.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extraColors.cardBackground,
        title = {
            Text(
                text = loc.t("Update Current Balance", "လက်ကျန်ငွေ ပြင်ဆင်ရန်"),
                fontWeight = FontWeight.Bold,
                color = extraColors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = loc.t(
                        "Set the actual balance for $walletName. Your total records and balance will be updated automatically.",
                        "$walletName အတွက် လက်ရှိ ပကတိလက်ကျန်ငွေကို ထည့်သွင်းပါ။ စုစုပေါင်းလက်ကျန်ငွေ အလိုအလျောက် ချိန်ညှိပေးပါမည်။"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = extraColors.textSecondary
                )
                OutlinedTextField(
                    value = newBalanceText,
                    onValueChange = { newBalanceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(loc.t("New Balance (MMK / ကျပ်)", "လက်ကျန်ငွေ ပမာဏ")) },
                    placeholder = { Text("e.g. 250000", color = placeholderColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_adjust_balance"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = extraColors.textPrimary,
                        unfocusedTextColor = extraColors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = unfocusedBorderColor,
                        focusedContainerColor = fieldContainerColor,
                        unfocusedContainerColor = fieldContainerColor,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = unfocusedLabelColor,
                        focusedPlaceholderColor = placeholderColor,
                        unfocusedPlaceholderColor = placeholderColor,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = newBalanceText.toLongOrNull() ?: 0L
                    onConfirm(bal)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(loc.t("Save Balance", "သိမ်းဆည်းမည်"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(loc.t("Cancel", "ပယ်ဖျက်"), color = unfocusedLabelColor, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun WalletDetailDialog(
    wallet: WalletWithBalance,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdjustBalance: (newBalance: Long) -> Unit = {}
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    val brandColor = CategoryIconHelper.parseColor(wallet.colorHex)

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAdjustDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = extraColors.cardBackground,
            title = { 
                Text(
                    loc.t("Delete Wallet?", "ပိုက်ဆံအိတ် ဖျက်ရန် သေချာပါသလား?"),
                    fontWeight = FontWeight.Bold,
                    color = extraColors.textPrimary
                ) 
            },
            text = { 
                Text(
                    loc.t("Are you sure you want to delete ${wallet.name}?", "${wallet.name} ကို ဖျက်ရန် သေချာပါသလား?"),
                    color = extraColors.textSecondary
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividCoral)
                ) {
                    Text(loc.t("Delete", "ဖျက်မည်"), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(loc.t("Cancel", "ပယ်ဖျက်"), color = extraColors.textSecondary)
                }
            }
        )
        return
    }

    if (showAdjustDialog) {
        AdjustBalanceDialog(
            walletName = wallet.name,
            currentBalance = wallet.currentBalance,
            onDismiss = { showAdjustDialog = false },
            onConfirm = { newBal ->
                showAdjustDialog = false
                onAdjustBalance(newBal)
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = extraColors.cardBackground,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brandColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getWalletIcon(wallet.iconKey, wallet.type),
                                contentDescription = wallet.name,
                                tint = brandColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = wallet.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = extraColors.textPrimary
                            )
                            if (wallet.accountNumber.isNotBlank()) {
                                Text(
                                    text = formatMaskedAccountNumber(wallet.accountNumber),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textMuted
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = extraColors.textSecondary)
                    }
                }

                // Balance Card with Quick Adjust Action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(extraColors.cardElevated)
                        .clickable { showAdjustDialog = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = loc.t("Current Balance (Tap to edit)", "လက်ရှိ လက်ကျန်ငွေ (ပြင်ရန်နှိပ်ပါ)"),
                                style = MaterialTheme.typography.labelSmall,
                                color = extraColors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatMMK(wallet.currentBalance, loc.isBurmese()),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (wallet.currentBalance >= 0) extraColors.textPrimary else VividCoral
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Update Balance",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = loc.t("Update", "ပြင်ဆင်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Stats: Inflows & Outflows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricEmerald.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = loc.t("Total In", "စုစုပေါင်း ဝင်ငွေ"), style = MaterialTheme.typography.labelSmall, color = extraColors.textSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = CurrencyFormatter.formatCompactMMK(wallet.totalIncome, loc.isBurmese()), fontWeight = FontWeight.Bold, color = ElectricEmerald)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(VividCoral.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = loc.t("Total Out", "စုစုပေါင်း အသုံး"), style = MaterialTheme.typography.labelSmall, color = extraColors.textSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = CurrencyFormatter.formatCompactMMK(wallet.totalExpense, loc.isBurmese()), fontWeight = FontWeight.Bold, color = VividCoral)
                        }
                    }
                }

                // Actions: Edit & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, extraColors.border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = extraColors.cardElevated,
                            contentColor = extraColors.textPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = extraColors.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Edit", "ပြင်ဆင်မည်"),
                            color = extraColors.textPrimary,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VividCoral.copy(alpha = 0.2f))
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = VividCoral, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(loc.t("Delete", "ဖျက်မည်"), color = VividCoral)
                    }
                }
            }
        }
    }
}
