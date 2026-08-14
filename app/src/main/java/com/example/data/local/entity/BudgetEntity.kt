package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val categoryId: String,
    val monthlyLimit: Long, // in MMK
    val monthYear: String, // e.g. "2026-08" or "DEFAULT"
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING",
    val isDirty: Boolean = true
)
