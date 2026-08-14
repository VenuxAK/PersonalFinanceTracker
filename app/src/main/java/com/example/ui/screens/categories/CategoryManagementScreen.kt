package com.example.ui.screens.categories

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.domain.CategoryLocalization
import com.example.domain.LocalAppLocalization
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryManagementScreen(
    categories: List<CategoryEntity>,
    onAddCategory: (CategoryEntity) -> Unit,
    onUpdateCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    var selectedTab by remember { mutableStateOf("EXPENSE") }
    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    var categoryNameInput by remember { mutableStateOf("") }
    var selectedIconKey by remember { mutableStateOf("shopping") }
    var selectedColorHex by remember { mutableStateOf("#EF4444") }

    val filteredCategories = categories.filter { it.type == selectedTab }

    val availableColors = listOf(
        "#EF4444", "#F59E0B", "#10B981", "#0284C7", "#8B5CF6",
        "#EC4899", "#6366F1", "#14B8A6", "#84CC16", "#F97316"
    )

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
                        text = loc.t("Manage Categories", "အမျိုးအစား စီမံခန့်ခွဲမှု"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = extraColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = loc.t("Customize icons, colors & categories", "အမျိုးအစားများ၊ အရောင်နှင့် သင်္ကေတများ စိတ်ကြိုက်ပြင်ရန်"),
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .clickable {
                            editingCategory = null
                            categoryNameInput = ""
                            selectedIconKey = "shopping"
                            selectedColorHex = if (selectedTab == "INCOME") "#10B981" else "#EF4444"
                            showDialog = true
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("btn_new_category"),
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
                            text = loc.t("Add New", "အသစ်ထည့်"),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Tab Selector (Expense / Income)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(extraColors.cardElevated)
                    .padding(4.dp)
            ) {
                // Expense Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == "EXPENSE") VividCoral else Color.Transparent)
                        .clickable { selectedTab = "EXPENSE" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_cat_expense"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (selectedTab == "EXPENSE") Color.White else extraColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Expense Categories", "အသုံးစရိတ် အမျိုးအစား"),
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == "EXPENSE") Color.White else extraColors.textSecondary
                        )
                    }
                }

                // Income Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == "INCOME") ElectricEmerald else Color.Transparent)
                        .clickable { selectedTab = "INCOME" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_cat_income"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (selectedTab == "INCOME") Color.White else extraColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Income Categories", "ဝင်ငွေ အမျိုးအစား"),
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == "INCOME") Color.White else extraColors.textSecondary
                        )
                    }
                }
            }
        }

        // Category Items
        items(filteredCategories, key = { it.id }) { cat ->
            val catColor = CategoryIconHelper.parseColor(cat.colorHex)
            val catName = CategoryLocalization.getLocalizedCategoryName(cat.name, loc.isBurmese())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(extraColors.cardBackground)
                    .border(1.dp, extraColors.border, RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("category_row_${cat.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(catColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(cat.iconKey),
                                contentDescription = null,
                                tint = catColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = extraColors.textPrimary
                            )
                            if (cat.isDefault) {
                                Text(
                                    text = loc.t("Default Category", "မူလ သတ်မှတ်ချက်"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textMuted
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                editingCategory = cat
                                categoryNameInput = cat.name
                                selectedIconKey = cat.iconKey
                                selectedColorHex = cat.colorHex
                                showDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Category",
                                tint = extraColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (!cat.isDefault) {
                            IconButton(
                                onClick = { onDeleteCategory(cat.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Category",
                                    tint = VividCoral,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = extraColors.cardBackground,
            title = {
                Text(
                    text = if (editingCategory == null) loc.t("New Category", "အမျိုးအစား အသစ်")
                    else loc.t("Edit Category", "အမျိုးအစား ပြင်ဆင်ရန်"),
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = loc.t("Category Name", "အမျိုးအစား အမည်"),
                        style = MaterialTheme.typography.labelMedium,
                        color = extraColors.textSecondary
                    )

                    OutlinedTextField(
                        value = categoryNameInput,
                        onValueChange = { categoryNameInput = it },
                        singleLine = true,
                        placeholder = { Text(loc.t("e.g. Coffee, Gym, Petrol", "ဥပမာ - ကော်ဖီ၊ အားကစား"), color = extraColors.textMuted) },
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
                        modifier = Modifier.fillMaxWidth().testTag("input_category_name")
                    )

                    Text(
                        text = loc.t("Select Icon", "သင်္ကေတ ရွေးချယ်ပါ"),
                        style = MaterialTheme.typography.labelMedium,
                        color = extraColors.textSecondary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (iconKey in CategoryIconHelper.availableIcons) {
                            val isSel = selectedIconKey == iconKey
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else extraColors.cardElevated)
                                    .border(
                                        1.dp,
                                        if (isSel) MaterialTheme.colorScheme.primary else extraColors.border,
                                        CircleShape
                                    )
                                    .clickable { selectedIconKey = iconKey },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconHelper.getIcon(iconKey),
                                    contentDescription = null,
                                    tint = if (isSel) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = loc.t("Select Color", "အရောင် ရွေးချယ်ပါ"),
                        style = MaterialTheme.typography.labelMedium,
                        color = extraColors.textSecondary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (hex in availableColors) {
                            val color = CategoryIconHelper.parseColor(hex)
                            val isSel = selectedColorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (categoryNameInput.isNotBlank()) {
                            if (editingCategory == null) {
                                onAddCategory(
                                    CategoryEntity(
                                        id = UUID.randomUUID().toString(),
                                        name = categoryNameInput.trim(),
                                        iconKey = selectedIconKey,
                                        colorHex = selectedColorHex,
                                        type = selectedTab,
                                        isDefault = false
                                    )
                                )
                            } else {
                                onUpdateCategory(
                                    editingCategory!!.copy(
                                        name = categoryNameInput.trim(),
                                        iconKey = selectedIconKey,
                                        colorHex = selectedColorHex
                                    )
                                )
                            }
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("btn_save_category")
                ) {
                    Text(loc.t("Save", "သိမ်းမည်"), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(loc.t("Cancel", "ပယ်ဖျက်"), color = extraColors.textSecondary)
                }
            }
        )
    }
}
