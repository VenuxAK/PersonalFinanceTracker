package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianBg

enum class NavScreen(
    val titleEn: String,
    val titleMm: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", "ပင်မစာမျက်နှာ", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    ANALYTICS("Analytics", "သုံးသပ်ချက်", Icons.Filled.PieChart, Icons.Outlined.PieChart, "nav_analytics"),
    RECURRING("Recurring", "ပုံမှန်ပေးချေမှု", Icons.Filled.Autorenew, Icons.Outlined.Autorenew, "nav_recurring"),
    CATEGORIES("Categories", "အမျိုးအစားများ", Icons.Filled.Category, Icons.Outlined.Category, "nav_categories"),
    SETTINGS("Settings", "ဆက်တင်များ", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

@Composable
fun BalanceaBottomNav(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = extraColors.cardBackground.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, extraColors.border),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Screen 0: Dashboard
            NavItem(
                screen = NavScreen.DASHBOARD,
                isSelected = currentScreen == NavScreen.DASHBOARD,
                onClick = { onScreenSelected(NavScreen.DASHBOARD) },
                modifier = Modifier.weight(1f)
            )

            // Screen 1: Analytics
            NavItem(
                screen = NavScreen.ANALYTICS,
                isSelected = currentScreen == NavScreen.ANALYTICS,
                onClick = { onScreenSelected(NavScreen.ANALYTICS) },
                modifier = Modifier.weight(1f)
            )

            // Center Quick Add FAB
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricEmerald, NeonViolet)
                            )
                        )
                        .clickable { onAddClick() }
                        .testTag("fab_quick_add"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Transaction",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Screen 2: Recurring
            NavItem(
                screen = NavScreen.RECURRING,
                isSelected = currentScreen == NavScreen.RECURRING,
                onClick = { onScreenSelected(NavScreen.RECURRING) },
                modifier = Modifier.weight(1f)
            )

            // Screen 3: Settings
            NavItem(
                screen = NavScreen.SETTINGS,
                isSelected = currentScreen == NavScreen.SETTINGS,
                onClick = { onScreenSelected(NavScreen.SETTINGS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KorenBottomNav(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BalanceaBottomNav(
        currentScreen = currentScreen,
        onScreenSelected = onScreenSelected,
        onAddClick = onAddClick,
        modifier = modifier
    )
}

@Composable
private fun NavItem(
    screen: NavScreen,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    val label = if (loc.isBurmese()) screen.titleMm else screen.titleEn

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material3.ripple(bounded = true, radius = 28.dp),
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .testTag(screen.testTag)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                contentDescription = label,
                tint = if (isSelected) primaryColor else extraColors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) primaryColor else extraColors.textMuted,
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
