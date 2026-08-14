package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.LocalExtraColors

enum class NavScreen(val titleEn: String, val titleMy: String) {
    DASHBOARD("Home", "ပင်မ"),
    ANALYTICS("Analytics", "သုံးသပ်ချက်"),
    RECURRING("Plans", "ပုံမှန်စာရင်း"),
    SETTINGS("Settings", "ဆက်တင်"),
    CATEGORY_MGMT("Categories", "အမျိုးအစား")
}

@Composable
fun KorenBottomNav(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Nav Bar Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x66000000))
                .clip(RoundedCornerShape(26.dp))
                .background(extraColors.cardElevated.copy(alpha = 0.96f))
                .border(1.dp, extraColors.border, RoundedCornerShape(26.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Dashboard / Home Tab
            NavTabItem(
                selected = currentScreen == NavScreen.DASHBOARD,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                label = loc.t(NavScreen.DASHBOARD.titleEn, NavScreen.DASHBOARD.titleMy),
                onClick = { onScreenSelected(NavScreen.DASHBOARD) },
                testTag = "nav_dashboard",
                modifier = Modifier.weight(1f)
            )

            // 2. Analytics Tab
            NavTabItem(
                selected = currentScreen == NavScreen.ANALYTICS,
                selectedIcon = Icons.Filled.Equalizer,
                unselectedIcon = Icons.Outlined.Equalizer,
                label = loc.t(NavScreen.ANALYTICS.titleEn, NavScreen.ANALYTICS.titleMy),
                onClick = { onScreenSelected(NavScreen.ANALYTICS) },
                testTag = "nav_analytics",
                modifier = Modifier.weight(1f)
            )

            // 3. Center FAB for Add Transaction (Steady non-shifting action button)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = Color.White)
                    ) { onAddClick() }
                    .testTag("nav_add_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = loc.t("Add Transaction", "စာရင်းအသစ်"),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. Subscriptions / Plans Tab
            NavTabItem(
                selected = currentScreen == NavScreen.RECURRING,
                selectedIcon = Icons.Filled.Autorenew,
                unselectedIcon = Icons.Outlined.Autorenew,
                label = loc.t(NavScreen.RECURRING.titleEn, NavScreen.RECURRING.titleMy),
                onClick = { onScreenSelected(NavScreen.RECURRING) },
                testTag = "nav_recurring",
                modifier = Modifier.weight(1f)
            )

            // 5. Settings Tab
            NavTabItem(
                selected = currentScreen == NavScreen.SETTINGS || currentScreen == NavScreen.CATEGORY_MGMT,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                label = loc.t(NavScreen.SETTINGS.titleEn, NavScreen.SETTINGS.titleMy),
                onClick = { onScreenSelected(NavScreen.SETTINGS) },
                testTag = "nav_settings",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavTabItem(
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val extraColors = LocalExtraColors.current
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else extraColors.textSecondary,
        label = "tab_content_color"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 26.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
