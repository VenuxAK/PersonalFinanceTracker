package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.SyncState
import com.example.domain.LocalAppLocalization
import com.example.domain.rememberLocalizationState
import com.example.ui.components.KorenBottomNav
import com.example.ui.components.NavScreen
import com.example.ui.screens.analytics.AnalyticsScreen
import com.example.ui.screens.categories.CategoryManagementScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.recurring.RecurringScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.transaction.AddEditTransactionSheet
import com.example.ui.theme.KorenFinanceTheme
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val locState = rememberLocalizationState()

            CompositionLocalProvider(LocalAppLocalization provides locState) {
                KorenFinanceTheme(themeMode = locState.currentThemeMode) {
                    val coroutineScope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }

                    var currentScreen by remember { mutableStateOf(NavScreen.DASHBOARD) }

                    // Transaction Bottom Sheet State
                    var showTransactionSheet by remember { mutableStateOf(false) }
                    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
                    var initialIncomeMode by remember { mutableStateOf(false) }

                    // ViewModel States
                    val summary by viewModel.financialSummary.collectAsStateWithLifecycle()
                    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
                    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
                    val categories by viewModel.categories.collectAsStateWithLifecycle()
                    val budgets by viewModel.budgetsWithSpending.collectAsStateWithLifecycle()
                    val recurringTransactions by viewModel.recurringTransactions.collectAsStateWithLifecycle()
                    val categoryBreakdowns by viewModel.categoryBreakdown.collectAsStateWithLifecycle()
                    val cashflowPoints by viewModel.cashflowPoints.collectAsStateWithLifecycle()
                    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

                    // Sync result notifications
                    LaunchedEffect(syncState) {
                        when (syncState) {
                            is SyncState.Success -> {
                                snackbarHostState.showSnackbar(
                                    message = locState.t("Cloud sync successful", "Cloud အရန်သိမ်းဆည်းခြင်း အောင်မြင်ပါသည်"),
                                    duration = SnackbarDuration.Short
                                )
                                viewModel.resetSyncState()
                            }
                            is SyncState.Error -> {
                                snackbarHostState.showSnackbar(
                                    message = locState.t("Operating in local offline mode", "ဒေသတွင်း အော့ဖ်လိုင်းစနစ်ဖြင့် သိမ်းဆည်းထားပါသည်"),
                                    duration = SnackbarDuration.Short
                                )
                                viewModel.resetSyncState()
                            }
                            else -> Unit
                        }
                    }

                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            KorenBottomNav(
                                currentScreen = currentScreen,
                                onScreenSelected = { currentScreen = it },
                                onAddClick = {
                                    editingTransaction = null
                                    initialIncomeMode = false
                                    showTransactionSheet = true
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = innerPadding.calculateTopPadding())
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    NavScreen.DASHBOARD -> {
                                        DashboardScreen(
                                            summary = summary,
                                            recentTransactions = recentTransactions,
                                            budgets = budgets,
                                            cashflowPoints = cashflowPoints,
                                            syncState = syncState,
                                            onAddTransactionClick = { isIncome ->
                                                editingTransaction = null
                                                initialIncomeMode = isIncome
                                                showTransactionSheet = true
                                            },
                                            onSyncClick = {
                                                viewModel.triggerCloudSync()
                                            },
                                            onNavigateAnalytics = {
                                                currentScreen = NavScreen.ANALYTICS
                                            },
                                            onNavigateSubscriptions = {
                                                currentScreen = NavScreen.RECURRING
                                            },
                                            onTransactionClick = { tx ->
                                                editingTransaction = tx
                                                showTransactionSheet = true
                                            }
                                        )
                                    }
                                    NavScreen.ANALYTICS -> {
                                        AnalyticsScreen(
                                            categoryBreakdowns = categoryBreakdowns,
                                            cashflowPoints = cashflowPoints,
                                            budgets = budgets,
                                            allTransactions = allTransactions,
                                            categories = categories,
                                            onSetBudget = { catId, limit ->
                                                viewModel.setBudget(catId, limit)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        locState.t("Monthly budget updated", "လစဉ်ဘတ်ဂျက် သတ်မှတ်ပြီးပါပြီ")
                                                    )
                                                }
                                            },
                                            onTransactionClick = { tx ->
                                                editingTransaction = tx
                                                showTransactionSheet = true
                                            }
                                        )
                                    }
                                    NavScreen.RECURRING -> {
                                        RecurringScreen(
                                            recurringTransactions = recurringTransactions,
                                            onApplyRecurring = { recurringTx ->
                                                viewModel.applyRecurringTransaction(recurringTx)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        locState.t("Posted ${recurringTx.title} to ledger", "${recurringTx.title} ကို စာရင်းသွင်းပြီးပါပြီ")
                                                    )
                                                }
                                            },
                                            onAddRecurringClick = {
                                                editingTransaction = null
                                                initialIncomeMode = false
                                                showTransactionSheet = true
                                            },
                                            onEditRecurring = { tx ->
                                                editingTransaction = tx
                                                showTransactionSheet = true
                                            }
                                        )
                                    }
                                    NavScreen.SETTINGS -> {
                                        SettingsScreen(
                                            syncState = syncState,
                                            onSyncClick = { viewModel.triggerCloudSync() },
                                            onNavigateCategoryManagement = {
                                                currentScreen = NavScreen.CATEGORY_MGMT
                                            },
                                            totalTransactions = allTransactions.size,
                                            totalCategories = categories.size,
                                            totalBudgets = budgets.size
                                        )
                                    }
                                    NavScreen.CATEGORY_MGMT -> {
                                        CategoryManagementScreen(
                                            categories = categories,
                                            onAddCategory = { cat ->
                                                viewModel.addCategory(cat)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        locState.t("Category added", "အမျိုးအစား အသစ်ထည့်ပြီးပါပြီ")
                                                    )
                                                }
                                            },
                                            onUpdateCategory = { cat ->
                                                viewModel.updateCategory(cat)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        locState.t("Category updated", "အမျိုးအစား ပြင်ဆင်ပြီးပါပြီ")
                                                    )
                                                }
                                            },
                                            onDeleteCategory = { catId ->
                                                viewModel.deleteCategory(catId)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        locState.t("Category removed", "အမျိုးအစား ဖျက်ပြီးပါပြီ")
                                                    )
                                                }
                                            },
                                            onBack = {
                                                currentScreen = NavScreen.SETTINGS
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Modal Bottom Sheet for Add/Edit Transaction
                        if (showTransactionSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showTransactionSheet = false
                                    editingTransaction = null
                                },
                                sheetState = sheetState,
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                AddEditTransactionSheet(
                                    initialTransaction = editingTransaction,
                                    categories = categories,
                                    initialIsIncome = initialIncomeMode,
                                    onDismiss = {
                                        showTransactionSheet = false
                                        editingTransaction = null
                                    },
                                    onSave = { title, amount, type, catId, catName, catIcon, catColor, timestamp, note, isRecurring, frequency ->
                                        if (editingTransaction != null) {
                                            viewModel.updateTransaction(
                                                editingTransaction!!.copy(
                                                    title = title,
                                                    amount = amount,
                                                    type = type,
                                                    categoryId = catId,
                                                    categoryName = catName,
                                                    categoryIcon = catIcon,
                                                    categoryColor = catColor,
                                                    note = note,
                                                    isRecurring = isRecurring,
                                                    frequency = frequency
                                                )
                                            )
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    locState.t("Transaction updated", "စာရင်း ပြင်ဆင်ပြီးပါပြီ")
                                                )
                                            }
                                        } else {
                                            viewModel.addTransaction(
                                                title = title,
                                                amount = amount,
                                                type = type,
                                                categoryId = catId,
                                                categoryName = catName,
                                                categoryIcon = catIcon,
                                                categoryColor = catColor,
                                                timestamp = timestamp,
                                                note = note,
                                                isRecurring = isRecurring,
                                                frequency = frequency
                                            )
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    locState.t("Transaction added", "စာရင်းအသစ် ထည့်ပြီးပါပြီ")
                                                )
                                            }
                                        }
                                        showTransactionSheet = false
                                        editingTransaction = null
                                    },
                                    onDelete = { id ->
                                        viewModel.deleteTransaction(id)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                locState.t("Transaction deleted", "စာရင်း ဖျက်ပြီးပါပြီ")
                                            )
                                        }
                                        showTransactionSheet = false
                                        editingTransaction = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
