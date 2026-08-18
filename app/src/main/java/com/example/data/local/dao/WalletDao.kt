package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE isArchived = 0 ORDER BY isDefault DESC, createdAt ASC")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets WHERE id = :id")
    suspend fun getWalletById(id: String): WalletEntity?

    @Query("SELECT COUNT(*) FROM wallets")
    suspend fun getWalletCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallets(wallets: List<WalletEntity>)

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("DELETE FROM wallets WHERE id = :id")
    suspend fun deleteWalletById(id: String)

    @Query("DELETE FROM wallets")
    suspend fun deleteAllWallets()

    @Query("UPDATE wallets SET initialBalance = :newInitialBalance, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateWalletInitialBalance(id: String, newInitialBalance: Long, updatedAt: Long = System.currentTimeMillis())
}
