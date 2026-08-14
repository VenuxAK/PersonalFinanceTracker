package com.example.ui.screens.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.RecurrenceFrequency
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    categories: List<CategoryEntity>,
    editingTransaction: TransactionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        amount: Long,
        type: String,
        categoryId: String,
        categoryName: String,
        categoryIcon: String,
        categoryColor: String,
        timestamp: Long,
        note: String,
        isRecurring: Boolean,
        frequency: String,
        nextDueDate: Long,
        autoApply: Boolean
    ) -> Unit,
    onUpdate: ((TransactionEntity) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var selectedType by remember { mutableStateOf(editingTransaction?.type ?: "EXPENSE") }
    var amountInput by remember { mutableStateOf(editingTransaction?.amount?.toString() ?: "") }
    var titleInput by remember { mutableStateOf(editingTransaction?.title ?: "") }
    var noteInput by remember { mutableStateOf(editingTransaction?.note ?: "") }

    val filteredCategories = categories.filter { it.type == selectedType }
    var selectedCategoryId by remember {
        mutableStateOf(
            editingTransaction?.categoryId ?: categories.firstOrNull { it.type == selectedType }?.id ?: ""
        )
    }

    var isRecurring by remember { mutableStateOf(editingTransaction?.isRecurring ?: false) }
    var frequency by remember { mutableStateOf(editingTransaction?.frequency ?: RecurrenceFrequency.MONTHLY.name) }
    var autoApply by remember { mutableStateOf(editingTransaction?.autoApply ?: false) }

    val selectedCategory = categories.find { it.id == selectedCategoryId }
        ?: filteredCategories.firstOrNull()
        ?: categories.firstOrNull()

    val isIncome = selectedType == "INCOME"
    val accentColor = if (isIncome) ElectricEmerald else VividCoral

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = extraColors.cardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(extraColors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (editingTransaction == null) {
                        loc.t("New Transaction", "စာရင်းအသစ်ထည့်ရန်")
                    } else {
                        loc.t("Edit Transaction", "စာရင်းပြင်ဆင်ရန်")
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    if (editingTransaction != null && onDelete != null) {
                        IconButton(
                            onClick = {
                                onDelete(editingTransaction.id)
                                onDismiss()
                            },
                            modifier = Modifier.testTag("btn_delete_transaction")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = VividCoral
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = extraColors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income / Expense Segmented Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(extraColors.cardElevated)
                    .padding(4.dp)
            ) {
                // Expense tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedType == "EXPENSE") VividCoral else Color.Transparent)
                        .clickable {
                            selectedType = "EXPENSE"
                            val firstExp = categories.firstOrNull { it.type == "EXPENSE" }
                            if (firstExp != null) selectedCategoryId = firstExp.id
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_type_expense"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (selectedType == "EXPENSE") Color.White else extraColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Expense", "အသုံး"),
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == "EXPENSE") Color.White else extraColors.textSecondary
                        )
                    }
                }

                // Income tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedType == "INCOME") ElectricEmerald else Color.Transparent)
                        .clickable {
                            selectedType = "INCOME"
                            val firstInc = categories.firstOrNull { it.type == "INCOME" }
                            if (firstInc != null) selectedCategoryId = firstInc.id
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_type_income"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (selectedType == "INCOME") Color.White else extraColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Income", "အဝင်"),
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == "INCOME") Color.White else extraColors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Input
            Text(
                text = loc.t("Amount (MMK / Ks)", "ပမာဏ (ကျပ်)"),
                style = MaterialTheme.typography.labelMedium,
                color = extraColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountInput,
                onValueChange = { input ->
                    if (input.all { it.isDigit() } && input.length <= 12) {
                        amountInput = input
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "0 Ks",
                        color = extraColors.textMuted,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = extraColors.textPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = extraColors.textPrimary,
                    unfocusedTextColor = extraColors.textPrimary,
                    focusedContainerColor = extraColors.cardElevated,
                    unfocusedContainerColor = extraColors.cardElevated,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = extraColors.border,
                    cursorColor = accentColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_amount")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title / Description Input
            Text(
                text = loc.t("Description", "အကြောင်းအရာ"),
                style = MaterialTheme.typography.labelMedium,
                color = extraColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                singleLine = true,
                placeholder = {
                    Text(
                        text = if (isIncome) loc.t("e.g. Monthly Salary, Freelance", "ဥပမာ - လစာ၊ အလွတ်တန်းဝင်ငွေ")
                        else loc.t("e.g. Lunch with team, Groceries", "ဥပမာ - နေ့လယ်စာ၊ ကုန်စုံဆိုင်"),
                        color = extraColors.textMuted
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Medium
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = extraColors.textPrimary,
                    unfocusedTextColor = extraColors.textPrimary,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = extraColors.border,
                    focusedContainerColor = extraColors.cardElevated,
                    unfocusedContainerColor = extraColors.cardElevated,
                    cursorColor = accentColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_title")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection Chips
            Text(
                text = loc.t("Category", "အမျိုးအစား"),
                style = MaterialTheme.typography.labelMedium,
                color = extraColors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredCategories.forEach { category ->
                    val isSelected = category.id == selectedCategoryId
                    val catColor = CategoryIconHelper.parseColor(category.colorHex)
                    val catName = CategoryLocalization.getLocalizedCategoryName(category.name, loc.isBurmese())

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) catColor.copy(alpha = 0.2f) else extraColors.cardElevated)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) catColor else extraColors.border,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategoryId = category.id }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("chip_category_${category.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(catColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(category.iconKey),
                                contentDescription = null,
                                tint = catColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = catName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) extraColors.textPrimary else extraColors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note Input
            Text(
                text = loc.t("Note (Optional)", "မှတ်စု (ရွေးချယ်ရန်)"),
                style = MaterialTheme.typography.labelMedium,
                color = extraColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = {
                    Text(
                        text = loc.t("Add additional notes...", "အသေးစိတ်မှတ်စုများ ထည့်ပါ..."),
                        color = extraColors.textMuted
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = extraColors.textPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = extraColors.textPrimary,
                    unfocusedTextColor = extraColors.textPrimary,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = extraColors.border,
                    focusedContainerColor = extraColors.cardElevated,
                    unfocusedContainerColor = extraColors.cardElevated,
                    cursorColor = accentColor
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_note")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recurring Options Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(extraColors.cardElevated)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = loc.t("Recurring Payment", "ပုံမှန်ပေးချေမှု"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = extraColors.textPrimary
                        )
                        Text(
                            text = loc.t("Auto schedule future cycles", "နောင်ကာလများအတွက် သတ်မှတ်ရန်"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }
                Switch(
                    checked = isRecurring,
                    onCheckedChange = { isRecurring = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            if (isRecurring) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        RecurrenceFrequency.WEEKLY to loc.t("Weekly", "အပတ်စဉ်"),
                        RecurrenceFrequency.MONTHLY to loc.t("Monthly", "လစဉ်"),
                        RecurrenceFrequency.YEARLY to loc.t("Yearly", "နှစ်စဉ်")
                    ).forEach { (freq, label) ->
                        val isFreqSelected = frequency == freq.name
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isFreqSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else extraColors.cardElevated)
                                .border(
                                    1.dp,
                                    if (isFreqSelected) MaterialTheme.colorScheme.primary else extraColors.border,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { frequency = freq.name }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isFreqSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isFreqSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save / Submit Button
            Button(
                onClick = {
                    val amountLong = amountInput.toLongOrNull() ?: 0L
                    if (amountLong > 0 && selectedCategory != null) {
                        val finalTitle = titleInput.ifBlank {
                            CategoryLocalization.getLocalizedCategoryName(selectedCategory.name, loc.isBurmese())
                        }
                        if (editingTransaction != null && onUpdate != null) {
                            onUpdate(
                                editingTransaction.copy(
                                    title = finalTitle,
                                    amount = amountLong,
                                    type = selectedType,
                                    categoryId = selectedCategory.id,
                                    categoryName = selectedCategory.name,
                                    categoryIcon = selectedCategory.iconKey,
                                    categoryColor = selectedCategory.colorHex,
                                    note = noteInput,
                                    isRecurring = isRecurring,
                                    frequency = frequency,
                                    autoApply = autoApply
                                )
                            )
                        } else {
                            onSave(
                                finalTitle,
                                amountLong,
                                selectedType,
                                selectedCategory.id,
                                selectedCategory.name,
                                selectedCategory.iconKey,
                                selectedCategory.colorHex,
                                System.currentTimeMillis(),
                                noteInput,
                                isRecurring,
                                frequency,
                                System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                                autoApply
                            )
                        }
                        onDismiss()
                    }
                },
                enabled = (amountInput.toLongOrNull() ?: 0L) > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_transaction")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (editingTransaction == null) loc.t("Save Transaction", "မှတ်တမ်းတင်မည်")
                    else loc.t("Update Record", "ပြင်ဆင်ချက်သိမ်းမည်"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
