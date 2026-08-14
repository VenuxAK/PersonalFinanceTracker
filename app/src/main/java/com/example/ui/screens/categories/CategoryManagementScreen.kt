package com.example.ui.screens.categories

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    categories: List<CategoryEntity>,
    onAddCategory: (CategoryEntity) -> Unit,
    onUpdateCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    var selectedTypeTab by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var deletingCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    val filteredCategories = categories.filter { it.type.equals(selectedTypeTab, ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_back_category_mgmt")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = extraColors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = loc.t("CATEGORY MANAGEMENT", "အမျိုးအစား စီမံခန့်ခွဲမှု"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = loc.t("Customize Categories", "အသုံးစရိတ်/ဝင်ငွေ အမျိုးအစားများ"),
                        style = MaterialTheme.typography.titleLarge,
                        color = extraColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Type Segmented Switcher (Expense / Income)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Expense Tab
                val isExp = selectedTypeTab == "EXPENSE"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isExp) extraColors.cardElevated else extraColors.cardBackground)
                        .border(
                            1.dp,
                            if (isExp) VividCoral else extraColors.border,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedTypeTab = "EXPENSE" }
                        .padding(vertical = 12.dp)
                        .testTag("tab_cat_expense"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = loc.t("Expense Categories", "အသုံးစရိတ် အမျိုးအစား"),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isExp) VividCoral else extraColors.textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Income Tab
                val isInc = selectedTypeTab == "INCOME"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isInc) extraColors.cardElevated else extraColors.cardBackground)
                        .border(
                            1.dp,
                            if (isInc) ElectricEmerald else extraColors.border,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedTypeTab = "INCOME" }
                        .padding(vertical = 12.dp)
                        .testTag("tab_cat_income"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = loc.t("Income Categories", "ဝင်ငွေ အမျိုးအစား"),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isInc) ElectricEmerald else extraColors.textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Add New Category Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(extraColors.cardElevated)
                    .border(1.dp, extraColors.borderLight, RoundedCornerShape(16.dp))
                    .clickable {
                        editingCategory = null
                        showAddEditDialog = true
                    }
                    .padding(16.dp)
                    .testTag("btn_add_new_category"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = loc.t("+ Create New Category", "+ အမျိုးအစား အသစ်ထည့်ရန်"),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // List of Categories
        items(filteredCategories, key = { it.id }) { category ->
            val catColor = CategoryIconHelper.parseColor(category.colorHex)
            val localizedName = CategoryLocalization.getLocalizedCategoryName(category.name, loc.isBurmese())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(extraColors.cardBackground)
                    .border(1.dp, extraColors.border, RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(catColor.copy(alpha = 0.18f))
                                .border(1.dp, catColor.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(category.iconKey),
                                contentDescription = category.name,
                                tint = catColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = localizedName,
                                style = MaterialTheme.typography.titleMedium,
                                color = extraColors.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (loc.isBurmese() && localizedName != category.name) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extraColors.textMuted
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                editingCategory = category
                                showAddEditDialog = true
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = extraColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { deletingCategory = category },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = VividCoral.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Category Dialog
    if (showAddEditDialog) {
        CategoryEditorDialog(
            category = editingCategory,
            initialType = selectedTypeTab,
            onDismiss = {
                showAddEditDialog = false
                editingCategory = null
            },
            onSave = { name, iconKey, colorHex, type ->
                if (editingCategory != null) {
                    onUpdateCategory(
                        editingCategory!!.copy(
                            name = name,
                            iconKey = iconKey,
                            colorHex = colorHex,
                            type = type
                        )
                    )
                } else {
                    onAddCategory(
                        CategoryEntity(
                            id = "cat_custom_${UUID.randomUUID().toString().take(8)}",
                            name = name,
                            iconKey = iconKey,
                            colorHex = colorHex,
                            type = type
                        )
                    )
                }
                showAddEditDialog = false
                editingCategory = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingCategory != null) {
        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = {
                Text(
                    text = loc.t("Delete Category?", "အမျိုးအစား ဖျက်မည်လား?"),
                    color = extraColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = loc.t(
                        "Are you sure you want to remove '${deletingCategory!!.name}'? Transactions will remain in ledger under this category.",
                        "'${deletingCategory!!.name}' ကို ဖျက်ရန် သေချာပါသလား? ယခင်စာရင်းများသည် ကျန်ရှိနေမည်ဖြစ်သည်။"
                    ),
                    color = extraColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(deletingCategory!!.id)
                        deletingCategory = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VividCoral)
                ) {
                    Text(loc.t("Delete", "ဖျက်မည်"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) {
                    Text(loc.t("Cancel", "မလုပ်တော့ပါ"), color = extraColors.textSecondary)
                }
            },
            containerColor = extraColors.cardBackground
        )
    }
}

@Composable
private fun CategoryEditorDialog(
    category: CategoryEntity?,
    initialType: String,
    onDismiss: () -> Unit,
    onSave: (name: String, iconKey: String, colorHex: String, type: String) -> Unit
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(category?.iconKey ?: "shopping_bag") }
    var selectedColorHex by remember { mutableStateOf(category?.colorHex ?: "#10B981") }
    var selectedType by remember { mutableStateOf(category?.type ?: initialType) }

    val iconOptions = listOf(
        "shopping_bag", "restaurant", "directions_car", "home", "bolt",
        "movie", "medical_services", "school", "flight", "spa",
        "payments", "laptop_mac", "trending_up", "storefront", "redeem", "account_balance", "work"
    )

    val colorOptions = listOf(
        "#00E599", "#10B981", "#38BDF8", "#8B5CF6", "#F59E0B",
        "#FF4D4D", "#EC4899", "#6366F1", "#14B8A6", "#64748B"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (category != null) loc.t("Edit Category", "အမျိုးအစား ပြင်ဆင်ရန်") else loc.t("New Category", "အမျိုးအစား အသစ်ထည့်ရန်"),
                color = extraColors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(loc.t("Category Name", "အမည်"), color = extraColors.textSecondary) },
                    placeholder = { Text(loc.t("e.g. Coffee, Gym, Petrol", "ဥပမာ - ကော်ဖီ၊ အားကစား"), color = extraColors.textMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = extraColors.textPrimary,
                        unfocusedTextColor = extraColors.textPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = extraColors.border
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_category_name")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Icon Picker
                Text(
                    text = loc.t("CHOOSE ICON", "အိုင်ကွန် ရွေးချယ်ပါ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(iconOptions) { iconKey ->
                        val isSelected = selectedIcon == iconKey
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else extraColors.cardElevated)
                                .border(
                                    1.5.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else extraColors.border,
                                    CircleShape
                                )
                                .clickable { selectedIcon = iconKey },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(iconKey),
                                contentDescription = iconKey,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color Picker
                Text(
                    text = loc.t("CHOOSE COLOR", "အရောင် ရွေးချယ်ပါ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(colorOptions) { colorHex ->
                        val color = CategoryIconHelper.parseColor(colorHex)
                        val isSelected = selectedColorHex.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    2.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedColorHex = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
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
                    if (name.isNotBlank()) {
                        onSave(name.trim(), selectedIcon, selectedColorHex, selectedType)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = loc.t("Save Category", "သိမ်းဆည်းမည်"),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(loc.t("Cancel", "မလုပ်တော့ပါ"), color = extraColors.textSecondary)
            }
        },
        containerColor = extraColors.cardBackground
    )
}
