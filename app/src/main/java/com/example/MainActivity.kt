package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.SyncState
import com.example.domain.LocalAppLocalization
import com.example.domain.rememberLocalizationState
import com.example.ui.components.BalanceaBottomNav
import com.example.ui.components.NavScreen
import com.example.ui.screens.analytics.AnalyticsScreen
import com.example.ui.screens.categories.CategoryManagementScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.recurring.RecurringScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.transaction.AddEditTransactionSheet
import com.example.ui.theme.BalanceaTheme
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val locState = rememberLocalizationState()

            CompositionLocalProvider(LocalAppLocalization provides locState) {
                BalanceaTheme(themeMode = locState.currentThemeMode) {
                    val coroutineScope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }

                    var currentScreen by remember { mutableStateOf(NavScreen.DASHBOARD) }
                    var showAddEditSheet by remember { mutableStateOf(false) }
                    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

                    // VM state
                    val financialSummary by viewModel.financialSummary.collectAsState()
                    val cashflowPoints by viewModel.cashflowPoints.collectAsState()
                    val recentTransactions by viewModel.recentTransactions.collectAsState()
                    val categories by viewModel.categories.collectAsState()
                    val budgets by viewModel.budgetsWithSpending.collectAsState()
                    val recurringTransactions by viewModel.recurringTransactions.collectAsState()
                    val categoryBreakdown by viewModel.categoryBreakdown.collectAsState()
                    val syncState by viewModel.syncState.collectAsState()

                    // Sync Snackbar feedback
                    LaunchedEffect(syncState) {
                        when (syncState) {
                            is SyncState.Success -> {
                                snackbarHostState.showSnackbar(
                                    if (locState.isBurmese()) "စာရင်းများ အောင်မြင်စွာ သိမ်းဆည်းပြီးပါပြီ (${(syncState as SyncState.Success).count} ခု)"
                                    else "Successfully synced ${(syncState as SyncState.Success).count} records to Cloud"
                                )
                                viewModel.resetSyncState()
                            }
                            is SyncState.Error -> {
                                snackbarHostState.showSnackbar(
                                    if (locState.isBurmese()) "အော့ဖ်လိုင်းဖြစ်နေပါသည် (အွန်လိုင်းရောက်လျှင် အလိုအလျောက် သိမ်းဆည်းပေးမည်)"
                                    else "Offline: Changes saved locally. Will sync when online."
                                )
                                viewModel.resetSyncState()
                            }
                            else -> {}
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            BalanceaBottomNav(
                                currentScreen = currentScreen,
                                onScreenSelected = { currentScreen = it },
                                onAddClick = {
                                    editingTransaction = null
                                    showAddEditSheet = true
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                NavScreen.DASHBOARD -> {
                                    DashboardScreen(
                                        summary = financialSummary,
                                        cashflowPoints = cashflowPoints,
                                        recentTransactions = recentTransactions,
                                        budgets = budgets,
                                        syncState = syncState,
                                        onSyncClick = { viewModel.triggerCloudSync() },
                                        onTransactionClick = { tx ->
                                            editingTransaction = tx
                                            showAddEditSheet = true
                                        },
                                        onSeeAllTransactions = {
                                            currentScreen = NavScreen.ANALYTICS
                                        }
                                    )
                                }

                                NavScreen.ANALYTICS -> {
                                    AnalyticsScreen(
                                        summary = financialSummary,
                                        categoryBreakdown = categoryBreakdown,
                                        budgets = budgets,
                                        allCategories = categories,
                                        onSetBudget = { catId, limit ->
                                            viewModel.setBudget(catId, limit)
                                        },
                                        onDeleteBudget = { catId ->
                                            viewModel.deleteBudget(catId)
                                        }
                                    )
                                }

                                NavScreen.RECURRING -> {
                                    RecurringScreen(
                                        recurringTransactions = recurringTransactions,
                                        onApplyRecurring = { tx ->
                                            viewModel.applyRecurringTransaction(tx)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (locState.isBurmese()) "စာရင်းသို့ ပေါင်းထည့်ပြီးပါပြီ"
                                                    else "Recorded to ledger!"
                                                )
                                            }
                                        },
                                        onAddRecurringClick = {
                                            editingTransaction = null
                                            showAddEditSheet = true
                                        },
                                        onEditRecurring = { tx ->
                                            editingTransaction = tx
                                            showAddEditSheet = true
                                        }
                                    )
                                }

                                NavScreen.CATEGORIES -> {
                                    CategoryManagementScreen(
                                        categories = categories,
                                        onAddCategory = { viewModel.addCategory(it) },
                                        onUpdateCategory = { viewModel.updateCategory(it) },
                                        onDeleteCategory = { viewModel.deleteCategory(it) }
                                    )
                                }

                                NavScreen.SETTINGS -> {
                                    SettingsScreen(
                                        syncState = syncState,
                                        onSyncClick = { viewModel.triggerCloudSync() },
                                        onNavigateCategories = {
                                            currentScreen = NavScreen.CATEGORIES
                                        }
                                    )
                                }
                            }
                        }

                        // Add/Edit Transaction Bottom Sheet
                        if (showAddEditSheet) {
                            AddEditTransactionSheet(
                                categories = categories,
                                editingTransaction = editingTransaction,
                                onDismiss = {
                                    showAddEditSheet = false
                                    editingTransaction = null
                                },
                                onSave = { title, amount, type, catId, catName, catIcon, catColor, timestamp, note, isRec, freq, nextDue, autoApp ->
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
                                        isRecurring = isRec,
                                        frequency = freq,
                                        nextDueDate = nextDue,
                                        autoApply = autoApp
                                    )
                                },
                                onUpdate = { updatedTx ->
                                    viewModel.updateTransaction(updatedTx)
                                },
                                onDelete = { txId ->
                                    viewModel.deleteTransaction(txId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
