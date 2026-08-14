package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.domain.AppThemeMode

private val BalanceaDarkColorScheme = darkColorScheme(
    primary = ElectricEmeraldDark,
    onPrimary = ObsidianBg,
    primaryContainer = SlateCardElevated,
    onPrimaryContainer = ElectricEmeraldDark,
    secondary = CyberBlue,
    onSecondary = ObsidianBg,
    secondaryContainer = SlateCard,
    onSecondaryContainer = CyberBlue,
    tertiary = NeonViolet,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = TextPrimaryDark,
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = SlateBorder,
    outlineVariant = SlateBorderLight,
    error = VividCoral,
    onError = Color.White
)

private val BalanceaLightColorScheme = lightColorScheme(
    primary = ElectricEmerald,
    onPrimary = Color.White,
    primaryContainer = LightCardElevated,
    onPrimaryContainer = ElectricEmerald,
    secondary = CyberBlue,
    onSecondary = Color.White,
    secondaryContainer = LightCard,
    onSecondaryContainer = CyberBlue,
    tertiary = NeonViolet,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightCardElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = LightBorderLight,
    error = VividCoral,
    onError = Color.White
)

data class ExtraThemeColors(
    val cardBackground: Color,
    val cardElevated: Color,
    val border: Color,
    val borderLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalExtraColors = staticCompositionLocalOf {
    ExtraThemeColors(
        cardBackground = SlateCard,
        cardElevated = SlateCardElevated,
        border = SlateBorder,
        borderLight = SlateBorderLight,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textMuted = TextMutedDark,
        isDark = true
    )
}

@Composable
fun BalanceaTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme: ColorScheme = if (isDark) BalanceaDarkColorScheme else BalanceaLightColorScheme
    val extraColors = if (isDark) {
        ExtraThemeColors(
            cardBackground = SlateCard,
            cardElevated = SlateCardElevated,
            border = SlateBorder,
            borderLight = SlateBorderLight,
            textPrimary = TextPrimaryDark,
            textSecondary = TextSecondaryDark,
            textMuted = TextMutedDark,
            isDark = true
        )
    } else {
        ExtraThemeColors(
            cardBackground = LightCard,
            cardElevated = LightCardElevated,
            border = LightBorder,
            borderLight = LightBorderLight,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textMuted = TextMutedLight,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Backward compatibility alias
@Composable
fun KorenFinanceTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    BalanceaTheme(themeMode = themeMode, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BalanceaTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        content = content
    )
}
