package com.example.ui.screens.transaction

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
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
    initialTransaction: TransactionEntity? = null,
    categories: List<CategoryEntity>,
    initialIsIncome: Boolean = false,
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
        frequency: String
    ) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var isIncome by remember {
        mutableStateOf(
            if (initialTransaction != null) initialTransaction.type == "INCOME" else initialIsIncome
        )
    }

    var title by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var amountInput by remember {
        mutableStateOf(if (initialTransaction != null) initialTransaction.amount.toString() else "")
    }
    var note by remember { mutableStateOf(initialTransaction?.note ?: "") }

    val filteredCategories = categories.filter { it.type.equals(if (isIncome) "INCOME" else "EXPENSE", ignoreCase = true) }
    var selectedCategory by remember(isIncome, categories) {
        mutableStateOf(
            initialTransaction?.let { tx -> categories.find { it.id == tx.categoryId } }
                ?: filteredCategories.firstOrNull()
        )
    }

    var isRecurring by remember { mutableStateOf(initialTransaction?.isRecurring ?: false) }
    var selectedFrequency by remember {
        mutableStateOf(
            try {
                RecurrenceFrequency.valueOf(initialTransaction?.frequency ?: "MONTHLY")
            } catch (e: Exception) {
                RecurrenceFrequency.MONTHLY
            }
        )
    }

    val parsedAmount = amountInput.toLongOrNull() ?: 0L

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        // Top Bar: Drag handle & Title & Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (initialTransaction == null) {
                    loc.t("Add Record", "စာရင်းအသစ်ထည့်ရန်")
                } else {
                    loc.t("Edit Record", "စာရင်းပြင်ဆင်ရန်")
                },
                style = MaterialTheme.typography.titleLarge,
                color = extraColors.textPrimary,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (initialTransaction != null && onDelete != null) {
                    IconButton(
                        onClick = { onDelete(initialTransaction.id) },
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

        Spacer(modifier = Modifier.height(12.dp))

        // Income / Expense Segmented Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(extraColors.cardElevated)
                .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            // Expense Option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isIncome) VividCoral else Color.Transparent)
                    .clickable {
                        isIncome = false
                        selectedCategory = categories.firstOrNull { it.type == "EXPENSE" }
                    }
                    .padding(vertical = 10.dp)
                    .testTag("tab_type_expense"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = loc.t("Expense (-)", "အသုံးစရိတ် (-)"),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (!isIncome) Color.White else extraColors.textSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Income Option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isIncome) ElectricEmerald else Color.Transparent)
                    .clickable {
                        isIncome = true
                        selectedCategory = categories.firstOrNull { it.type == "INCOME" }
                    }
                    .padding(vertical = 10.dp)
                    .testTag("tab_type_income"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = loc.t("Income (+)", "ဝင်ငွေ (+)"),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isIncome) MaterialTheme.colorScheme.onPrimary else extraColors.textSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Amount Display & Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(extraColors.cardElevated)
                .border(
                    1.5.dp,
                    if (isIncome) ElectricEmerald.copy(alpha = 0.5f) else VividCoral.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${if (isIncome) "+" else "-"} ${CurrencyFormatter.formatMMK(parsedAmount)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (isIncome) ElectricEmerald else VividCoral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 12) {
                            amountInput = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.headlineMedium,
                            color = extraColors.textMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center,
                        color = extraColors.textPrimary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = if (isIncome) ElectricEmerald else VividCoral
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_amount")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Amount Increment Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chips = listOf(10000L, 50000L, 100000L, 500000L)
            chips.forEach { addVal ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(extraColors.cardBackground)
                        .border(1.dp, extraColors.border, RoundedCornerShape(12.dp))
                        .clickable {
                            val curr = amountInput.toLongOrNull() ?: 0L
                            amountInput = (curr + addVal).toString()
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${CurrencyFormatter.formatMMKCompact(addVal)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title / Description Input
        Text(
            text = loc.t("TITLE / DESCRIPTION", "ခေါင်းစဉ် / အကြောင်းအရာ"),
            style = MaterialTheme.typography.labelSmall,
            color = extraColors.textMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = {
                Text(
                    loc.t("e.g. Grocery Shopping, Monthly Salary", "ဥပမာ - ကုန်စုံဆိုင်၊ လစာငွေ"),
                    color = extraColors.textMuted
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = extraColors.textPrimary,
                unfocusedTextColor = extraColors.textPrimary,
                focusedBorderColor = if (isIncome) ElectricEmerald else VividCoral,
                unfocusedBorderColor = extraColors.border,
                focusedContainerColor = extraColors.cardBackground,
                unfocusedContainerColor = extraColors.cardBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .testTag("input_title")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Category Selection Flow
        Text(
            text = loc.t("SELECT CATEGORY", "အမျိုးအစား ရွေးချယ်ပါ"),
            style = MaterialTheme.typography.labelSmall,
            color = extraColors.textMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            filteredCategories.forEach { category ->
                val isSelected = selectedCategory?.id == category.id
                val catColor = CategoryIconHelper.parseColor(category.colorHex)
                val localizedName = CategoryLocalization.getLocalizedCategoryName(category.name, loc.isBurmese())

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) catColor.copy(alpha = 0.2f) else extraColors.cardBackground)
                        .border(
                            1.5.dp,
                            if (isSelected) catColor else extraColors.border,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("category_chip_${category.id}")
                ) {
                    Icon(
                        imageVector = CategoryIconHelper.getIcon(category.iconKey),
                        contentDescription = category.name,
                        tint = if (isSelected) catColor else extraColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = localizedName,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) extraColors.textPrimary else extraColors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Optional Note Input
        Text(
            text = loc.t("OPTIONAL NOTE", "မှတ်စု (မထည့်လည်းရသည်)"),
            style = MaterialTheme.typography.labelSmall,
            color = extraColors.textMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = {
                Text(
                    loc.t("Add memo or transaction reference", "အခြားမှတ်ချက် သို့မဟုတ် ပြေစာအမှတ်"),
                    color = extraColors.textMuted
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = extraColors.textPrimary,
                unfocusedTextColor = extraColors.textPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = extraColors.border,
                focusedContainerColor = extraColors.cardBackground,
                unfocusedContainerColor = extraColors.cardBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .testTag("input_note")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Make Recurring (Subscription) Card with HIGH VISIBILITY TOGGLE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(extraColors.cardBackground)
                .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = loc.t("Make Recurring (Subscription)", "ပုံမှန်ပေးသွင်းစာရင်း အဖြစ်သတ်မှတ်မည်"),
                            style = MaterialTheme.typography.titleMedium,
                            color = extraColors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = loc.t("Auto calculate regular cycles and dues", "ပုံမှန်ကျသင့်ငွေနှင့် ရက်စွဲများ တွက်ချက်ပေးမည်"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textSecondary
                        )
                    }

                    // High contrast switch visible in both checked & unchecked states
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricEmerald,
                            uncheckedThumbColor = if (extraColors.isDark) Color(0xFFCBD5E1) else Color(0xFF64748B),
                            uncheckedTrackColor = if (extraColors.isDark) Color(0xFF333D4F) else Color(0xFFE2E8F0),
                            uncheckedBorderColor = if (extraColors.isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("switch_recurring")
                    )
                }

                AnimatedVisibility(visible = isRecurring) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = loc.t("BILLING FREQUENCY", "ပုံမှန်ကာလ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val frequencies = listOf(
                                RecurrenceFrequency.DAILY,
                                RecurrenceFrequency.WEEKLY,
                                RecurrenceFrequency.MONTHLY,
                                RecurrenceFrequency.YEARLY
                            )
                            frequencies.forEach { freq ->
                                val isFreqSelected = selectedFrequency == freq
                                val freqLabel = when (freq) {
                                    RecurrenceFrequency.DAILY -> loc.t("Daily", "နေ့စဉ်")
                                    RecurrenceFrequency.WEEKLY -> loc.t("Weekly", "အပတ်စဉ်")
                                    RecurrenceFrequency.MONTHLY -> loc.t("Monthly", "လစဉ်")
                                    RecurrenceFrequency.YEARLY -> loc.t("Yearly", "နှစ်စဉ်")
                                    else -> freq.displayName
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isFreqSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else extraColors.cardElevated)
                                        .border(
                                            1.dp,
                                            if (isFreqSelected) MaterialTheme.colorScheme.primary else extraColors.border,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedFrequency = freq }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = freqLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isFreqSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
                                        fontWeight = if (isFreqSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                if (parsedAmount > 0 && title.isNotBlank()) {
                    val finalCat = selectedCategory ?: CategoryEntity("cat_other_exp", "Other", "more_horiz", "#64748B", if (isIncome) "INCOME" else "EXPENSE")
                    onSave(
                        title.trim(),
                        parsedAmount,
                        if (isIncome) "INCOME" else "EXPENSE",
                        finalCat.id,
                        finalCat.name,
                        finalCat.iconKey,
                        finalCat.colorHex,
                        System.currentTimeMillis(),
                        note.trim(),
                        isRecurring,
                        if (isRecurring) selectedFrequency.name else "NONE"
                    )
                }
            },
            enabled = parsedAmount > 0 && title.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_save_transaction"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isIncome) ElectricEmerald else VividCoral,
                disabledContainerColor = extraColors.cardElevated
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (parsedAmount > 0 && title.isNotBlank()) Color.White else extraColors.textMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (initialTransaction != null) loc.t("Update Record", "စာရင်းပြင်ဆင်သိမ်းဆည်းမည်") else loc.t("Save Transaction", "စာရင်းအသစ် သိမ်းဆည်းမည်"),
                style = MaterialTheme.typography.titleMedium,
                color = if (parsedAmount > 0 && title.isNotBlank()) Color.White else extraColors.textMuted,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
