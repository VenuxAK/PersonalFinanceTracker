# Koren Finance (ကိုရန်း ဘဏ္ဍာရေး) 💰🇲🇲

An offline-first, bilingual personal finance and expense tracking Android application tailored for **Myanmar Kyat (MMK)**, built with modern **Jetpack Compose**, **Room Database**, **Firebase Cloud Firestore**, and **Jetpack DataStore**.

---

## ✨ Features

- **🇲🇲 MMK Currency & Dual-Language Support**:
  - Full support for formatted Myanmar Kyat (e.g., `1,250,000 Ks`, `1.25M Ks`).
  - Instant bilingual localization toggle between **English** and **Burmese (မြန်မာစာ)**.
- **⚡ Offline-First Architecture**:
  - Transactions, recurring subscriptions, and category spending budgets persist locally via **Room SQLite Database**.
  - Operates completely without an active internet connection and syncs whenever online.
- **☁️ Cloud Backup & Synchronization**:
  - Two-way delta synchronization with **Firebase Cloud Firestore** (`users/{userId}/transactions` and `users/{userId}/budgets`).
- **📊 Cash Flow & Expense Visualizations**:
  - Custom Compose Canvas charts for 7-day cash flow trends (Income vs. Expense curves).
  - Category spending breakdown with progress rings and alert thresholds.
- **🎯 Monthly Budgets & Alert Indicators**:
  - Set per-category monthly limits with real-time visual progress bars (Safe, Warning, Exceeded).
- **🔄 Recurring Payments & Subscriptions**:
  - Track fixed commitments (Rent, Wi-Fi, Salary, Utilities) with customizable billing cycles (Daily, Weekly, Monthly, Yearly).
  - One-tap quick post to ledger when dues arrive.
- **🎨 Modern Material 3 & Theme Support**:
  - **Dark Obsidian Theme**, **Clean Light Theme**, and System Default.
  - Preferences persisted via **Jetpack DataStore**.

---

## 🛠 Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture pattern with StateFlow & Coroutines
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) + SQLite with KSP
- **Cloud Backend**: [Firebase Cloud Firestore](https://firebase.google.com/docs/firestore)
- **Preference Storage**: [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Dependency Injection**: Clean Constructor Injection with Android ViewModel Provider

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                      # Main Activity entry point & navigation host
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt               # Room database setup & initial pre-populated categories
│   │   ├── dao/
│   │   │   ├── BudgetDao.kt             # Budget database access operations
│   │   │   ├── CategoryDao.kt           # Category management DAO
│   │   │   └── TransactionDao.kt        # Transaction queries & sync state updates
│   │   └── entity/
│   │       ├── BudgetEntity.kt
│   │       ├── CategoryEntity.kt
│   │       └── TransactionEntity.kt
│   ├── model/
│   │   └── FinancialModels.kt           # SyncState, RecurrenceFrequency, BudgetWithSpending models
│   ├── remote/
│   │   └── FirestoreSyncService.kt      # Two-way sync engine for Firebase Firestore
│   └── repository/
│       └── FinanceRepository.kt         # Single source of truth repository
├── domain/
│   ├── CurrencyFormatter.kt             # MMK compact & standard formatting logic
│   ├── FinancialCalculations.kt         # Cashflow aggregation & monthly summary engine
│   └── Localization.kt                  # DataStore-backed English & Burmese state manager
└── ui/
    ├── components/
    │   ├── BudgetProgressBar.kt         # Visual budget tracker with status tints
    │   ├── CategoryIconHelper.kt        # Dynamic Material Symbols mapper & color parser
    │   ├── CustomCashFlowChart.kt       # Bezier curve cash flow canvas visualization
    │   ├── KorenBottomNav.kt            # Bottom navigation bar with central quick-add FAB
    │   └── TransactionCard.kt           # Transaction item card with cloud sync status badges
    ├── screens/
    │   ├── analytics/AnalyticsScreen.kt # Charts, breakdowns, and spending limits
    │   ├── categories/CategoryManagementScreen.kt # Custom categories CRUD screen
    │   ├── dashboard/DashboardScreen.kt # Home dashboard with summary cards & quick actions
    │   ├── recurring/RecurringScreen.kt # Subscriptions & scheduled cycles
    │   ├── settings/SettingsScreen.kt   # Language, theme, and sync configuration
    │   └── transaction/AddEditTransactionSheet.kt # Modal bottom sheet for records
    ├── theme/
    │   ├── Color.kt                     # Emerald, Coral, Obsidian, Violet palettes
    │   ├── Theme.kt                     # Adaptive Light/Dark color schemes & typography
    │   └── Type.kt
    └── viewmodel/
        └── FinanceViewModel.kt          # UI state holders and business logic
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer
- JDK 17 or higher
- Android SDK (API 26 to API 35)

### 2. Clone the Repository
```bash
git clone https://github.com/your-username/koren-finance.git
cd koren-finance
```

### 3. Firebase Setup (Optional for Cloud Sync)
1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with the package name configured in `app/build.gradle.kts` (e.g. `com.aistudio.korenfinance`).
3. Download `google-services.json` and place it in the `/app` directory.
4. Enable **Cloud Firestore** in test or production mode with appropriate security rules.

### 4. Build and Run
```bash
./gradlew assembleDebug
```
Or open the project in **Android Studio** and click **Run**.

---

## 🔒 Firestore Security Rules (Example)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.
