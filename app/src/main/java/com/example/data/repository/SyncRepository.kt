package com.example.data.repository

import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.TransactionDao
import com.example.data.model.SyncState
import com.example.data.remote.FirestoreSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SyncRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val firestoreSyncService: FirestoreSyncService
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var lastSyncTime: Long = 0L

    suspend fun syncToCloud(userId: String = "user_koren_default"): Result<Int> {
        return withContext(Dispatchers.IO) {
            _syncState.value = SyncState.Syncing
            try {
                // 1. Gather pending local records
                val pendingTxs = transactionDao.getPendingTransactions()
                val pendingBudgets = budgetDao.getPendingBudgets()

                // Add small artificial delay so user can enjoy the smooth micro-animation feedback
                delay(600)

                // 2. Upload pending transactions and budgets to Firestore
                val txUploadCount = firestoreSyncService.uploadPendingTransactions(userId, pendingTxs)
                val budgetUploadCount = firestoreSyncService.uploadPendingBudgets(userId, pendingBudgets)

                // 3. Mark local records as Synced in Room
                if (pendingTxs.isNotEmpty()) {
                    transactionDao.markAsSynced(pendingTxs.map { it.id })
                }
                if (pendingBudgets.isNotEmpty()) {
                    budgetDao.markAsSynced(pendingBudgets.map { it.categoryId })
                }

                // 4. Pull newer remote transactions (Last-Write-Wins)
                val remoteTxs = firestoreSyncService.pullRemoteTransactions(userId, lastSyncTime)
                if (remoteTxs.isNotEmpty()) {
                    transactionDao.insertTransactions(remoteTxs)
                }

                val totalSynced = txUploadCount + budgetUploadCount + remoteTxs.size
                lastSyncTime = System.currentTimeMillis()

                _syncState.value = SyncState.Success(totalSynced, lastSyncTime)
                Result.success(totalSynced)
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Sync operation failed. Operating in offline mode."
                _syncState.value = SyncState.Error(errorMsg)
                Result.failure(e)
            }
        }
    }

    fun resetState() {
        _syncState.value = SyncState.Idle
    }
}
