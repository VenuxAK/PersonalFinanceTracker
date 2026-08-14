package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CategoryExpenseBreakdown
import com.example.domain.CategoryLocalization
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.LocalExtraColors
import kotlin.math.atan2
import kotlin.math.sqrt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomDonutChart(
    breakdowns: List<CategoryExpenseBreakdown>,
    modifier: Modifier = Modifier,
    totalExpense: Long = breakdowns.sumOf { it.totalAmount },
    showLegend: Boolean = false
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    if (breakdowns.isEmpty() || totalExpense == 0L) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = loc.t("No expenses recorded this period", "ယခုကာလအတွင်း အသုံးစရိတ် မရှိသေးပါ"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = extraColors.textMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = loc.t("Add expenses to see category breakdown", "အမျိုးအစားအလိုက် ခွဲခြမ်းကြည့်ရှုရန် အသုံးစရိတ်ထည့်ပါ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textSecondary
                )
            }
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(breakdowns) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Donut Canvas with Center Text
        Box(
            modifier = Modifier.size(210.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(210.dp)
                    .pointerInput(breakdowns) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = tapOffset.x - center.x
                            val dy = tapOffset.y - center.y
                            val dist = sqrt(dx * dx + dy * dy)
                            val outerRadius = size.width / 2f
                            val innerRadius = outerRadius - 32.dp.toPx()

                            if (dist in innerRadius..outerRadius) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f
                                val normalizedAngle = (angle + 90f) % 360f

                                var currentAngle = 0f
                                var foundIndex: Int? = null
                                for (i in breakdowns.indices) {
                                    val sweep = (breakdowns[i].percentage / 100f) * 360f
                                    if (normalizedAngle in currentAngle..(currentAngle + sweep)) {
                                        foundIndex = i
                                        break
                                    }
                                    currentAngle += sweep
                                }
                                selectedIndex = if (selectedIndex == foundIndex) null else foundIndex
                            } else {
                                selectedIndex = null
                            }
                        }
                    }
            ) {
                val strokeWidth = 22.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                val topLeft = Offset(center.x - radius, center.y - radius)
                val arcSize = Size(radius * 2, radius * 2)

                // Background track
                drawArc(
                    color = if (extraColors.isDark) Color(0xFF161A24) else Color(0xFFE2E8F0),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                var startAngle = -90f
                val gap = if (breakdowns.size > 1) 3f else 0f

                breakdowns.forEachIndexed { index, item ->
                    val color = CategoryIconHelper.parseColor(item.categoryColor)
                    val rawSweep = (item.percentage / 100f) * 360f
                    val sweep = (rawSweep - gap).coerceAtLeast(1f) * animationProgress.value

                    val isSelected = selectedIndex == index
                    val currentStroke = if (isSelected) strokeWidth + 4.dp.toPx() else strokeWidth

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(
                            width = currentStroke,
                            cap = StrokeCap.Round
                        )
                    )

                    startAngle += rawSweep
                }
            }

            // Center metric display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(140.dp)
                    .padding(horizontal = 8.dp)
            ) {
                if (selectedIndex != null && selectedIndex!! < breakdowns.size) {
                    val sel = breakdowns[selectedIndex!!]
                    val localizedCat = CategoryLocalization.getLocalizedCategoryName(sel.categoryName, loc.isBurmese())
                    Text(
                        text = localizedCat.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = extraColors.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = CurrencyFormatter.formatMMKCompact(sel.totalAmount),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = extraColors.textPrimary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${sel.percentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CategoryIconHelper.parseColor(sel.categoryColor)
                    )
                } else {
                    Text(
                        text = loc.t("TOTAL SPENT", "စုစုပေါင်းအသုံး"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = extraColors.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = CurrencyFormatter.formatMMKCompact(totalExpense),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = extraColors.textPrimary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = loc.t("${breakdowns.size} Categories", "အမျိုးအစား ${breakdowns.size} ခု"),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = extraColors.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        if (showLegend) {
            Spacer(modifier = Modifier.height(16.dp))

            // Category Legend Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                breakdowns.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val itemColor = CategoryIconHelper.parseColor(item.categoryColor)
                    val localizedCat = CategoryLocalization.getLocalizedCategoryName(item.categoryName, loc.isBurmese())

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) extraColors.cardElevated else if (extraColors.isDark) Color(0xFF161922) else Color(0xFFF1F5F9))
                            .border(
                                1.dp,
                                if (isSelected) itemColor else extraColors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedIndex = if (selectedIndex == index) null else index
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(itemColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = localizedCat,
                            style = MaterialTheme.typography.labelMedium,
                            color = extraColors.textPrimary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.percentage.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = extraColors.textSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
