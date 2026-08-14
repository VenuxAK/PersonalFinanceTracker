package com.example

import com.example.domain.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterUnitTest {

    @Test
    fun formatMMK_correctFormatting() {
        val amount = 1500000L
        val formatted = CurrencyFormatter.formatMMK(amount)
        assertEquals("1,500,000 Ks", formatted)
    }

    @Test
    fun formatMMK_withSign() {
        val income = CurrencyFormatter.formatMMK(50000L, withSign = true, isIncome = true)
        val expense = CurrencyFormatter.formatMMK(50000L, withSign = true, isIncome = false)
        assertEquals("+ 50,000 Ks", income)
        assertEquals("- 50,000 Ks", expense)
    }

    @Test
    fun formatMMKCompact_millionsAndThousands() {
        assertEquals("1.5M Ks", CurrencyFormatter.formatMMKCompact(1500000L))
        assertEquals("450K Ks", CurrencyFormatter.formatMMKCompact(450000L))
        assertEquals("500 Ks", CurrencyFormatter.formatMMKCompact(500L))
    }

    @Test
    fun parseAmount_handlesPunctuation() {
        assertEquals(1500000L, CurrencyFormatter.parseAmount("1,500,000"))
        assertEquals(25000L, CurrencyFormatter.parseAmount("25,000 Ks"))
    }
}
