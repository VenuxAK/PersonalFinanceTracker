package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.WalletDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.WalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, BudgetEntity::class, CategoryEntity::class, WalletEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "koren_finance_db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_WALLETS = listOf(
            WalletEntity(
                id = "wallet_kbzpay",
                name = "KBZPay",
                type = "MOBILE_WALLET",
                initialBalance = 750000L,
                colorHex = "#0066B2",
                iconKey = "phone_iphone",
                accountNumber = "09450012345",
                isDefault = true
            ),
            WalletEntity(
                id = "wallet_cash",
                name = "Cash (လက်ငင်းငွေ)",
                type = "CASH",
                initialBalance = 350000L,
                colorHex = "#10B981",
                iconKey = "payments",
                accountNumber = "",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_cbpay",
                name = "CB Pay",
                type = "MOBILE_WALLET",
                initialBalance = 300000L,
                colorHex = "#E65100",
                iconKey = "phone_iphone",
                accountNumber = "09250067890",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_ayapay",
                name = "AYA Pay",
                type = "MOBILE_WALLET",
                initialBalance = 200000L,
                colorHex = "#D32F2F",
                iconKey = "phone_iphone",
                accountNumber = "09790011223",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_uabpay",
                name = "UABpay (SaiSai)",
                type = "MOBILE_WALLET",
                initialBalance = 150000L,
                colorHex = "#6A1B9A",
                iconKey = "phone_iphone",
                accountNumber = "09960033445",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_yomapay",
                name = "YOMA Next / Pay",
                type = "MOBILE_WALLET",
                initialBalance = 180000L,
                colorHex = "#F57C00",
                iconKey = "phone_iphone",
                accountNumber = "09310055667",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_wavepay",
                name = "Wave Money",
                type = "MOBILE_WALLET",
                initialBalance = 120000L,
                colorHex = "#FBBF24",
                iconKey = "phone_iphone",
                accountNumber = "09770088990",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_kbzbank",
                name = "KBZ Bank",
                type = "BANK_ACCOUNT",
                initialBalance = 1200000L,
                colorHex = "#004C97",
                iconKey = "account_balance",
                accountNumber = "•••• 8912",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_cbbank",
                name = "CB Bank",
                type = "BANK_ACCOUNT",
                initialBalance = 500000L,
                colorHex = "#005BAA",
                iconKey = "account_balance",
                accountNumber = "•••• 4421",
                isDefault = false
            ),
            WalletEntity(
                id = "wallet_ayabank",
                name = "AYA Bank",
                type = "BANK_ACCOUNT",
                initialBalance = 400000L,
                colorHex = "#C62828",
                iconKey = "account_balance",
                accountNumber = "•••• 3190",
                isDefault = false
            )
        )

        val DEFAULT_CATEGORIES = listOf(
            // Expense Categories
            CategoryEntity("cat_food", "Food & Dining", "restaurant", "#F59E0B", "EXPENSE", true),
            CategoryEntity("cat_transport", "Transport", "directions_car", "#3B82F6", "EXPENSE", true),
            CategoryEntity("cat_housing", "Housing & Rent", "home", "#8B5CF6", "EXPENSE", true),
            CategoryEntity("cat_utilities", "Utilities", "bolt", "#EC4899", "EXPENSE", true),
            CategoryEntity("cat_shopping", "Shopping", "shopping_bag", "#10B981", "EXPENSE", true),
            CategoryEntity("cat_entertainment", "Entertainment", "movie", "#6366F1", "EXPENSE", true),
            CategoryEntity("cat_health", "Healthcare", "medical_services", "#EF4444", "EXPENSE", true),
            CategoryEntity("cat_education", "Education", "school", "#06B6D4", "EXPENSE", true),
            CategoryEntity("cat_travel", "Travel", "flight", "#F97316", "EXPENSE", true),
            CategoryEntity("cat_other_exp", "Other Expense", "more_horiz", "#64748B", "EXPENSE", true),
            
            // Income Categories
            CategoryEntity("cat_salary", "Salary", "payments", "#00E599", "INCOME", true),
            CategoryEntity("cat_freelance", "Freelance", "laptop_mac", "#38BDF8", "INCOME", true),
            CategoryEntity("cat_investment", "Investment", "trending_up", "#A78BFA", "INCOME", true),
            CategoryEntity("cat_business", "Business", "storefront", "#FBBF24", "INCOME", true),
            CategoryEntity("cat_other_inc", "Other Income", "redeem", "#34D399", "INCOME", true)
        )

        val DEFAULT_BUDGETS = listOf(
            BudgetEntity("cat_food", 350000L, "DEFAULT", System.currentTimeMillis(), "PENDING", true),
            BudgetEntity("cat_transport", 120000L, "DEFAULT", System.currentTimeMillis(), "PENDING", true),
            CategoryEntity("cat_utilities", "Utilities", "bolt", "#EC4899", "EXPENSE", true).let {
                BudgetEntity("cat_utilities", 80000L, "DEFAULT", System.currentTimeMillis(), "PENDING", true)
            },
            BudgetEntity("cat_shopping", 200000L, "DEFAULT", System.currentTimeMillis(), "PENDING", true),
            BudgetEntity("cat_entertainment", 100000L, "DEFAULT", System.currentTimeMillis(), "PENDING", true)
        )

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        if (database.categoryDao().getCategoryCount() == 0) {
                            populateDatabase(database)
                        } else if (database.walletDao().getWalletCount() == 0) {
                            database.walletDao().insertWallets(DEFAULT_WALLETS)
                        }
                    }
                }
            }

            suspend fun populateDatabase(database: AppDatabase) {
                database.walletDao().insertWallets(DEFAULT_WALLETS)
                database.categoryDao().insertCategories(DEFAULT_CATEGORIES)
                database.budgetDao().insertOrUpdateBudgets(DEFAULT_BUDGETS)
                
                // Add sample initial starter transactions in MMK for instant visual showcase
                val now = System.currentTimeMillis()
                val oneDay = 86400000L
                val sampleTxs = listOf(
                    TransactionEntity(
                        id = "sample_tx_1",
                        title = "Monthly Tech Salary",
                        amount = 1800000L,
                        type = "INCOME",
                        categoryId = "cat_salary",
                        categoryName = "Salary",
                        categoryIcon = "payments",
                        categoryColor = "#00E599",
                        timestamp = now - (oneDay * 6),
                        note = "Direct bank deposit",
                        isRecurring = true,
                        frequency = "MONTHLY",
                        nextDueDate = now + (oneDay * 24),
                        autoApply = true,
                        syncStatus = "SYNCED",
                        isDirty = false,
                        walletId = "wallet_kbzbank",
                        walletName = "KBZ Bank"
                    ),
                    TransactionEntity(
                        id = "sample_tx_2",
                        title = "Apartment Rent & Service",
                        amount = 450000L,
                        type = "EXPENSE",
                        categoryId = "cat_housing",
                        categoryName = "Housing & Rent",
                        categoryIcon = "home",
                        categoryColor = "#8B5CF6",
                        timestamp = now - (oneDay * 5),
                        note = "August rent payment",
                        isRecurring = true,
                        frequency = "MONTHLY",
                        nextDueDate = now + (oneDay * 25),
                        autoApply = true,
                        syncStatus = "SYNCED",
                        isDirty = false,
                        walletId = "wallet_kbzpay",
                        walletName = "KBZPay"
                    ),
                    TransactionEntity(
                        id = "sample_tx_3",
                        title = "Grocery & Dining Stock",
                        amount = 185000L,
                        type = "EXPENSE",
                        categoryId = "cat_food",
                        categoryName = "Food & Dining",
                        categoryIcon = "restaurant",
                        categoryColor = "#F59E0B",
                        timestamp = now - (oneDay * 3),
                        note = "Marketplace groceries and ingredients",
                        isRecurring = false,
                        syncStatus = "SYNCED",
                        isDirty = false,
                        walletId = "wallet_cash",
                        walletName = "Cash (လက်ငင်းငွေ)"
                    ),
                    TransactionEntity(
                        id = "sample_tx_4",
                        title = "Freelance Mobile UI Design",
                        amount = 650000L,
                        type = "INCOME",
                        categoryId = "cat_freelance",
                        categoryName = "Freelance",
                        categoryIcon = "laptop_mac",
                        categoryColor = "#38BDF8",
                        timestamp = now - (oneDay * 2),
                        note = "Client milestone payout",
                        isRecurring = false,
                        syncStatus = "PENDING",
                        isDirty = true,
                        walletId = "wallet_cbpay",
                        walletName = "CB Pay"
                    ),
                    TransactionEntity(
                        id = "sample_tx_5",
                        title = "Fiber Internet & Electricity",
                        amount = 65000L,
                        type = "EXPENSE",
                        categoryId = "cat_utilities",
                        categoryName = "Utilities",
                        categoryIcon = "bolt",
                        categoryColor = "#EC4899",
                        timestamp = now - (oneDay * 1),
                        note = "High speed broadband monthly fee",
                        isRecurring = true,
                        frequency = "MONTHLY",
                        nextDueDate = now + (oneDay * 29),
                        autoApply = true,
                        syncStatus = "PENDING",
                        isDirty = true,
                        walletId = "wallet_ayapay",
                        walletName = "AYA Pay"
                    ),
                    TransactionEntity(
                        id = "sample_tx_6",
                        title = "Fuel & Cab Rides",
                        amount = 45000L,
                        type = "EXPENSE",
                        categoryId = "cat_transport",
                        categoryName = "Transport",
                        categoryIcon = "directions_car",
                        categoryColor = "#3B82F6",
                        timestamp = now - (oneDay * 0),
                        note = "Weekly commute gas fill",
                        isRecurring = false,
                        syncStatus = "PENDING",
                        isDirty = true,
                        walletId = "wallet_cash",
                        walletName = "Cash (လက်ငင်းငွေ)"
                    )
                )
                database.transactionDao().insertTransactions(sampleTxs)
            }
        }
    }
}
