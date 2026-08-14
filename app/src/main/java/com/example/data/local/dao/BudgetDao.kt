package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY monthlyLimit DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    suspend fun getBudgetByCategoryId(categoryId: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE isDirty = 1 OR syncStatus = 'PENDING'")
    suspend fun getPendingBudgets(): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudgets(budgets: List<BudgetEntity>)

    @Query("DELETE FROM budgets WHERE categoryId = :categoryId")
    suspend fun deleteBudget(categoryId: String)

    @Query("UPDATE budgets SET syncStatus = 'SYNCED', isDirty = 0 WHERE categoryId IN (:categoryIds)")
    suspend fun markAsSynced(categoryIds: List<String>)
}
