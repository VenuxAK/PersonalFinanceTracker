package com.example.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

object CurrencyFormatter {
    private val decimalFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        DecimalFormat("#,##0", symbols)
    }

    /**
     * Formats amount in standard MMK style: e.g. "1,500,000 Ks"
     */
    fun formatMMK(amount: Long, includeSymbol: Boolean = true, withSign: Boolean = false, isIncome: Boolean? = null): String {
        val formattedNumber = decimalFormat.format(abs(amount))
        val sign = when {
            withSign && isIncome == true -> "+ "
            withSign && isIncome == false -> "- "
            withSign && amount > 0 -> "+ "
            withSign && amount < 0 -> "- "
            else -> ""
        }
        return if (includeSymbol) "$sign$formattedNumber Ks" else "$sign$formattedNumber"
    }

    /**
     * Formats amount with ISO code: e.g. "MMK 1,500,000"
     */
    fun formatMMKFull(amount: Long): String {
        return "MMK ${decimalFormat.format(abs(amount))}"
    }

    /**
     * Compact MMK formatting for charts/cards: e.g. "1.5M Ks", "450K Ks", "500 Ks"
     */
    fun formatMMKCompact(amount: Long): String {
        val absVal = abs(amount)
        return when {
            absVal >= 1_000_000_000 -> {
                val value = absVal / 1_000_000_000.0
                String.format(Locale.US, "%.1fB Ks", value)
            }
            absVal >= 1_000_000 -> {
                val value = absVal / 1_000_000.0
                String.format(Locale.US, "%.1fM Ks", value)
            }
            absVal >= 1_000 -> {
                val value = absVal / 1_000.0
                String.format(Locale.US, "%.0fK Ks", value)
            }
            else -> "$absVal Ks"
        }
    }

    /**
     * Parses user numeric inputs safely into Long
     */
    fun parseAmount(input: String): Long {
        val clean = input.filter { it.isDigit() }
        return clean.toLongOrNull() ?: 0L
    }
}
