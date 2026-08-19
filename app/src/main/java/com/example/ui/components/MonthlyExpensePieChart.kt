package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.CategoryExpenseBreakdown
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A custom Canvas-based Pie Chart component that visually summarizes total expenses
 * by category for the current month with interactive slice inspection and category legends.
 */
@Composable
fun MonthlyExpensePieChart(
    breakdowns: List<CategoryExpenseBreakdown>,
    totalExpense: Long,
    modifier: Modifier = Modifier,
    onCategoryClick: ((CategoryExpenseBreakdown) -> Unit)? = null
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current
    val density = LocalDensity.current

    val monthName = remember {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    // Chart slice animation progress
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(breakdowns) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(extraColors.cardBackground)
            .border(1.dp, extraColors.border, RoundedCornerShape(24.dp))
            .padding(18.dp)
            .testTag("card_monthly_expense_pie_chart")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Title, Month Name & Total Monthly Expense Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = loc.t("Monthly Expenses by Category", "ယခုလ အသုံးစရိတ် ခွဲခြမ်းစိတ်ဖြာမှု"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = extraColors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (loc.isBurmese()) "ယခုလ ကုန်ကျစရိတ် စုစုပေါင်း" else monthName,
                        style = MaterialTheme.typography.labelSmall,
                        color = extraColors.textSecondary
                    )
                }

                // Total Expense Pill Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(VividCoral.copy(alpha = 0.12f))
                        .border(1.dp, VividCoral.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.formatCompactMMK(totalExpense, loc.isBurmese()),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VividCoral
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (breakdowns.isEmpty() || totalExpense <= 0L) {
                // Empty State: Custom Canvas Dashed Circle & Helper Prompt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(extraColors.cardElevated)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val strokeColor = extraColors.border
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawCircle(
                            color = strokeColor,
                            radius = size.minDimension / 2f - 4.dp.toPx(),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(extraColors.cardBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = extraColors.textMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = loc.t("No expenses recorded this month", "ယခုလအတွက် အသုံးစရိတ် မှတ်တမ်း မရှိသေးပါ"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = extraColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loc.t("Add an expense to view category distribution", "အမျိုးအစားအလိုက် ခွဲခြမ်းကြည့်ရန် အသုံးစရိတ်ထည့်ပါ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textSecondary
                        )
                    }
                }
            } else {
                // Interactive Custom Canvas Pie Chart + Inspector Box
                val separatorColor = extraColors.cardBackground

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .testTag("canvas_pie_chart"),
                        contentAlignment = Alignment.Center
                    ) {
                        val marginPx = with(density) { 6.dp.toPx() }
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .pointerInput(breakdowns) {
                                    detectTapGestures { tapOffset ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val dx = tapOffset.x - center.x
                                        val dy = tapOffset.y - center.y
                                        val dist = sqrt(dx * dx + dy * dy)
                                        val minDim = min(size.width, size.height).toFloat()
                                        val radius = (minDim / 2f) - marginPx

                                        if (dist <= radius + with(density) { 12.dp.toPx() }) {
                                            // Compute angle in degrees starting from top (-90 degrees)
                                            var angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                            if (angleDeg < 0) angleDeg += 360f
                                            val normalizedAngle = (angleDeg + 90f) % 360f

                                            var currentAngle = 0f
                                            var matchedIdx: Int? = null
                                            for (i in breakdowns.indices) {
                                                val sweep = (breakdowns[i].percentage / 100f) * 360f
                                                if (normalizedAngle in currentAngle..(currentAngle + sweep)) {
                                                    matchedIdx = i
                                                    break
                                                }
                                                currentAngle += sweep
                                            }
                                            selectedIndex = if (selectedIndex == matchedIdx) null else matchedIdx
                                        } else {
                                            selectedIndex = null
                                        }
                                    }
                                }
                        ) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val baseRadius = (size.minDimension / 2f) - 8.dp.toPx()
                            val progress = animProgress.value

                            var currentStartAngle = -90f

                            // Draw each slice
                            breakdowns.forEachIndexed { index, item ->
                                val sweepAngle = (item.percentage / 100f) * 360f * progress
                                val sliceColor = CategoryIconHelper.parseColor(item.categoryColor)
                                val isSelected = selectedIndex == index

                                val effectiveRadius = if (isSelected) baseRadius + 6.dp.toPx() else baseRadius

                                // If slice is selected, slightly offset outward
                                val midAngleRad = ((currentStartAngle + sweepAngle / 2f) * PI / 180.0)
                                val sliceOffset = if (isSelected) {
                                    Offset(
                                        x = (cos(midAngleRad) * 6.dp.toPx()).toFloat(),
                                        y = (sin(midAngleRad) * 6.dp.toPx()).toFloat()
                                    )
                                } else {
                                    Offset.Zero
                                }

                                val sliceCenter = center + sliceOffset
                                val sliceTopLeft = Offset(
                                    sliceCenter.x - effectiveRadius,
                                    sliceCenter.y - effectiveRadius
                                )
                                val sliceSize = Size(effectiveRadius * 2, effectiveRadius * 2)

                                // Draw Pie Arc Slice
                                drawArc(
                                    color = sliceColor,
                                    startAngle = currentStartAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = true,
                                    topLeft = sliceTopLeft,
                                    size = sliceSize,
                                    style = Fill
                                )

                                // Draw slice divider line
                                if (breakdowns.size > 1) {
                                    val lineAngleRad = (currentStartAngle * PI / 180.0)
                                    val edgePoint = Offset(
                                        x = sliceCenter.x + (cos(lineAngleRad) * (effectiveRadius + 1.dp.toPx())).toFloat(),
                                        y = sliceCenter.y + (sin(lineAngleRad) * (effectiveRadius + 1.dp.toPx())).toFloat()
                                    )
                                    drawLine(
                                        color = separatorColor,
                                        start = sliceCenter,
                                        end = edgePoint,
                                        strokeWidth = 2.5.dp.toPx()
                                    )
                                }

                                // Highlight border for selected slice
                                if (isSelected) {
                                    drawArc(
                                        color = Color.White.copy(alpha = 0.9f),
                                        startAngle = currentStartAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = true,
                                        topLeft = sliceTopLeft,
                                        size = sliceSize,
                                        style = Stroke(width = 3.dp.toPx())
                                    )
                                }

                                currentStartAngle += (item.percentage / 100f) * 360f
                            }
                        }

                        // Decorative Center Mini Accent (creates a sleek modern look)
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(extraColors.cardBackground)
                                .border(2.dp, extraColors.border, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedIndex != null) {
                                    CategoryIconHelper.getIcon(breakdowns[selectedIndex!!].categoryIcon)
                                } else {
                                    Icons.Default.TouchApp
                                },
                                contentDescription = null,
                                tint = if (selectedIndex != null) {
                                    CategoryIconHelper.parseColor(breakdowns[selectedIndex!!].categoryColor)
                                } else {
                                    extraColors.textMuted
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Selected Category Inspector Card / Hint
                val selectedItem = selectedIndex?.let { breakdowns.getOrNull(it) }
                if (selectedItem != null) {
                    val catColor = CategoryIconHelper.parseColor(selectedItem.categoryColor)
                    val localizedName = CategoryLocalization.getLocalizedCategoryName(selectedItem.categoryName, loc.isBurmese())

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(catColor.copy(alpha = 0.12f))
                            .border(1.dp, catColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("card_selected_pie_slice")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(catColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = CategoryIconHelper.getIcon(selectedItem.categoryIcon),
                                        contentDescription = selectedItem.categoryName,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = localizedName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = extraColors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", selectedItem.percentage)}% ${loc.t("of monthly spend", "စုစုပေါင်းအသုံးမှ")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extraColors.textSecondary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = CurrencyFormatter.formatMMK(selectedItem.totalAmount, loc.isBurmese()),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = catColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(extraColors.cardElevated)
                                        .clickable { selectedIndex = null },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear selection",
                                        tint = extraColors.textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Tap instruction hint
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = extraColors.textMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.t("Tap any slice or category below to inspect details", "အသေးစိတ်ကြည့်ရှုရန် စက်ဝိုင်းအပိုင်းကို နှိပ်ပါ"),
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Legends & Spending Progress Breakdown List
                val displayedBreakdowns = if (isExpanded || breakdowns.size <= 4) {
                    breakdowns
                } else {
                    breakdowns.take(4)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    displayedBreakdowns.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index
                        val catColor = CategoryIconHelper.parseColor(item.categoryColor)
                        val localizedName = CategoryLocalization.getLocalizedCategoryName(item.categoryName, loc.isBurmese())

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) catColor.copy(alpha = 0.15f) else extraColors.cardElevated)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) catColor else extraColors.border.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedIndex = if (selectedIndex == index) null else index
                                    onCategoryClick?.invoke(item)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("pie_category_legend_${item.categoryId}")
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(catColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = CategoryIconHelper.getIcon(item.categoryIcon),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = localizedName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = extraColors.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        // Percentage Pill
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(catColor.copy(alpha = 0.18f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${String.format(Locale.US, "%.1f", item.percentage)}%",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = catColor
                                            )
                                        }
                                    }

                                    Text(
                                        text = CurrencyFormatter.formatMMK(item.totalAmount, loc.isBurmese()),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = extraColors.textPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Visual Proportion Bar
                                val animatedBarFraction by animateFloatAsState(
                                    targetValue = (item.percentage / 100f).coerceIn(0.01f, 1f) * animProgress.value,
                                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                                    label = "barFraction"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(extraColors.border.copy(alpha = 0.5f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(animatedBarFraction)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(catColor)
                                    )
                                }
                            }
                        }
                    }

                    // Expand / Collapse button if more than 4 categories
                    if (breakdowns.size > 4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { isExpanded = !isExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) {
                                    loc.t("Show Fewer Categories", "အနည်းငယ်သာ ပြရန်")
                                } else {
                                    loc.t("+ ${breakdowns.size - 4} More Categories", "+ နောက်ထပ် ${breakdowns.size - 4} မျိုး ကြည့်ရန်")
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
