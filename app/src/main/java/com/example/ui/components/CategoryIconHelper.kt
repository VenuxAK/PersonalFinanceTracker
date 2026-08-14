package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(iconKey: String): ImageVector {
        return when (iconKey.lowercase()) {
            "restaurant", "food", "dining" -> Icons.Default.Restaurant
            "directions_car", "transport", "car", "travel_gas" -> Icons.Default.DirectionsCar
            "home", "housing", "rent" -> Icons.Default.Home
            "bolt", "utilities", "electric" -> Icons.Default.Bolt
            "shopping_bag", "shopping" -> Icons.Default.ShoppingBag
            "movie", "entertainment" -> Icons.Default.Movie
            "medical_services", "health", "hospital" -> Icons.Default.MedicalServices
            "school", "education", "books" -> Icons.Default.School
            "flight", "travel" -> Icons.Default.Flight
            "spa", "personal" -> Icons.Default.Spa
            "payments", "salary", "cash" -> Icons.Default.Payments
            "laptop_mac", "freelance", "tech" -> Icons.Default.LaptopMac
            "trending_up", "investment", "stocks" -> Icons.Default.TrendingUp
            "storefront", "business" -> Icons.Default.Storefront
            "redeem", "gift", "bonus" -> Icons.Default.Redeem
            "work" -> Icons.Default.Work
            "account_balance", "bank" -> Icons.Default.AccountBalance
            else -> Icons.Default.MoreHoriz
        }
    }

    val availableIcons = listOf(
        "restaurant", "shopping_bag", "directions_car", "home",
        "bolt", "movie", "medical_services", "school",
        "flight", "spa", "payments", "laptop_mac",
        "trending_up", "storefront", "redeem", "work", "account_balance"
    )

    fun parseColor(colorHex: String, fallback: Color = Color(0xFF64748B)): Color {
        return try {
            val cleanHex = if (colorHex.startsWith("#")) colorHex.substring(1) else colorHex
            val colorLong = cleanHex.toLong(16)
            if (cleanHex.length == 6) {
                Color(0xFF000000 or colorLong)
            } else if (cleanHex.length == 8) {
                Color(colorLong)
            } else {
                fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }
}
