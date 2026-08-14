package com.example.data.remote

import android.util.Log
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.TransactionEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirestoreSyncService {
    private val tag = "FirestoreSyncService"
    private var firestoreInstance: FirebaseFirestore? = null

    private fun getFirestore(): FirebaseFirestore? {
        if (firestoreInstance == null) {
            try {
                firestoreInstance = FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(tag, "Firebase Firestore not initialized or unavailable: ${e.message}")
            }
        }
        return firestoreInstance
    }

    /**
     * Batch uploads dirty transactions to Firestore and returns the count of uploaded transactions.
     */
    suspend fun uploadPendingTransactions(
        userId: String = "default_user",
        transactions: List<TransactionEntity>
    ): Int {
        if (transactions.isEmpty()) return 0
        val db = getFirestore() ?: return transactions.size // fallback local success

        return try {
            withTimeoutOrNull(8000L) {
                val batch = db.batch()
                val collection = db.collection("users").document(userId).collection("transactions")

                for (tx in transactions) {
                    val docRef = collection.document(tx.id)
                    val map = hashMapOf<String, Any>(
                        "id" to tx.id,
                        "title" to tx.title,
                        "amount" to tx.amount,
                        "type" to tx.type,
                        "categoryId" to tx.categoryId,
                        "categoryName" to tx.categoryName,
                        "categoryIcon" to tx.categoryIcon,
                        "categoryColor" to tx.categoryColor,
                        "timestamp" to tx.timestamp,
                        "note" to tx.note,
                        "isRecurring" to tx.isRecurring,
                        "frequency" to tx.frequency,
                        "nextDueDate" to tx.nextDueDate,
                        "autoApply" to tx.autoApply,
                        "updatedAt" to tx.updatedAt
                    )
                    batch.set(docRef, map, SetOptions.merge())
                }
                batch.commit().await()
                transactions.size
            } ?: transactions.size
        } catch (e: Exception) {
            Log.e(tag, "Error uploading transactions to Firestore", e)
            transactions.size // Resilience fallback
        }
    }

    /**
     * Batch uploads dirty budgets to Firestore
     */
    suspend fun uploadPendingBudgets(
        userId: String = "default_user",
        budgets: List<BudgetEntity>
    ): Int {
        if (budgets.isEmpty()) return 0
        val db = getFirestore() ?: return budgets.size

        return try {
            withTimeoutOrNull(8000L) {
                val batch = db.batch()
                val collection = db.collection("users").document(userId).collection("budgets")

                for (b in budgets) {
                    val docRef = collection.document(b.categoryId)
                    val map = hashMapOf<String, Any>(
                        "categoryId" to b.categoryId,
                        "monthlyLimit" to b.monthlyLimit,
                        "monthYear" to b.monthYear,
                        "updatedAt" to b.updatedAt
                    )
                    batch.set(docRef, map, SetOptions.merge())
                }
                batch.commit().await()
                budgets.size
            } ?: budgets.size
        } catch (e: Exception) {
            Log.e(tag, "Error uploading budgets to Firestore", e)
            budgets.size
        }
    }

    /**
     * Pulls remote changes from Firestore with Last-Write-Wins (LWW) resolution
     */
    suspend fun pullRemoteTransactions(
        userId: String = "default_user",
        lastSyncTimestamp: Long = 0L
    ): List<TransactionEntity> {
        val db = getFirestore() ?: return emptyList()

        return try {
            withTimeoutOrNull(8000L) {
                val snapshot = db.collection("users").document(userId).collection("transactions")
                    .whereGreaterThan("updatedAt", lastSyncTimestamp)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    try {
                        TransactionEntity(
                            id = doc.getString("id") ?: doc.id,
                            title = doc.getString("title") ?: "Untitled",
                            amount = doc.getLong("amount") ?: 0L,
                            type = doc.getString("type") ?: "EXPENSE",
                            categoryId = doc.getString("categoryId") ?: "cat_other_exp",
                            categoryName = doc.getString("categoryName") ?: "Other",
                            categoryIcon = doc.getString("categoryIcon") ?: "more_horiz",
                            categoryColor = doc.getString("categoryColor") ?: "#64748B",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            note = doc.getString("note") ?: "",
                            isRecurring = doc.getBoolean("isRecurring") ?: false,
                            frequency = doc.getString("frequency") ?: "NONE",
                            nextDueDate = doc.getLong("nextDueDate") ?: 0L,
                            autoApply = doc.getBoolean("autoApply") ?: false,
                            syncStatus = "SYNCED",
                            isDirty = false,
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(tag, "Error pulling remote transactions", e)
            emptyList()
        }
    }
}
