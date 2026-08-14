package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CashflowDataPoint
import com.example.domain.CurrencyFormatter
import com.example.domain.LocalAppLocalization
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.LocalExtraColors
import com.example.ui.theme.VividCoral
import kotlin.math.max

@Composable
fun CustomCashFlowChart(
    dataPoints: List<CashflowDataPoint>,
    modifier: Modifier = Modifier
) {
    val loc = LocalAppLocalization.current
    val extraColors = LocalExtraColors.current

    if (dataPoints.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(extraColors.cardBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = loc.t("Not enough cash flow data yet", "စာရင်းအချက်အလက် မလုံလောက်သေးပါ"),
                style = MaterialTheme.typography.bodyMedium,
                color = extraColors.textMuted
            )
        }
        return
    }

    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(dataPoints) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    val maxVal = remember(dataPoints) {
        val highest = dataPoints.maxOfOrNull { max(it.income, it.expense) } ?: 100000L
        if (highest <= 0L) 100000L else (highest * 1.15).toLong()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(extraColors.cardBackground)
            .padding(18.dp)
    ) {
        // Chart Header & Inspection readout (Responsive flex header)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)) {
                Text(
                    text = loc.t("CASH FLOW TREND", "ဝင်ငွေ/ထွက်ငွေ လားရာ"),
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.textMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (selectedPointIndex != null && selectedPointIndex!! < dataPoints.size) {
                    val p = dataPoints[selectedPointIndex!!]
                    Text(
                        text = "${p.label}: +${CurrencyFormatter.formatMMKCompact(p.income)} / -${CurrencyFormatter.formatMMKCompact(p.expense)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = extraColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = loc.t("Income vs. Expenses (7 Days)", "ဝင်ငွေနှင့် အသုံးစရိတ် (၇ ရက်)"),
                        style = MaterialTheme.typography.titleSmall,
                        color = extraColors.textSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Legend indicators with fixed minimum size to prevent vertical wrapping
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ElectricEmerald)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = loc.t("Income", "ဝင်ငွေ"),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = extraColors.textSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VividCoral)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = loc.t("Expense", "အသုံး"),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = extraColors.textSecondary,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware-Accelerated Bezier Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(dataPoints) {
                    detectTapGestures { tapOffset ->
                        val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                        val index = ((tapOffset.x + (stepX / 2f)) / stepX).toInt().coerceIn(0, dataPoints.size - 1)
                        selectedPointIndex = if (selectedPointIndex == index) null else index
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val n = dataPoints.size
            if (n < 2) return@Canvas

            val stepX = width / (n - 1)
            val bottomY = height - 20f
            val topY = 20f
            val usableHeight = bottomY - topY

            // 1. Draw horizontal grid lines
            val gridCount = 3
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            for (i in 0..gridCount) {
                val y = topY + (usableHeight / gridCount) * i
                drawLine(
                    color = if (extraColors.isDark) Color(0xFF262C38) else Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashEffect
                )
            }

            fun getY(value: Long): Float {
                val ratio = (value.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                return bottomY - (ratio * usableHeight * animProgress.value)
            }

            // Build Income Bezier Path & Gradient
            val incomePath = Path()
            val incomeGradientPath = Path()

            val expensePath = Path()
            val expenseGradientPath = Path()

            // Initial points
            val firstIncomeY = getY(dataPoints[0].income)
            val firstExpenseY = getY(dataPoints[0].expense)

            incomePath.moveTo(0f, firstIncomeY)
            incomeGradientPath.moveTo(0f, bottomY)
            incomeGradientPath.lineTo(0f, firstIncomeY)

            expensePath.moveTo(0f, firstExpenseY)
            expenseGradientPath.moveTo(0f, bottomY)
            expenseGradientPath.lineTo(0f, firstExpenseY)

            for (i in 0 until n - 1) {
                val x1 = i * stepX
                val y1Income = getY(dataPoints[i].income)
                val y1Expense = getY(dataPoints[i].expense)

                val x2 = (i + 1) * stepX
                val y2Income = getY(dataPoints[i + 1].income)
                val y2Expense = getY(dataPoints[i + 1].expense)

                val cx = (x1 + x2) / 2f

                // Cubic Bezier curve for income
                incomePath.cubicTo(cx, y1Income, cx, y2Income, x2, y2Income)
                incomeGradientPath.cubicTo(cx, y1Income, cx, y2Income, x2, y2Income)

                // Cubic Bezier curve for expense
                expensePath.cubicTo(cx, y1Expense, cx, y2Expense, x2, y2Expense)
                expenseGradientPath.cubicTo(cx, y1Expense, cx, y2Expense, x2, y2Expense)
            }

            val lastX = (n - 1) * stepX
            incomeGradientPath.lineTo(lastX, bottomY)
            incomeGradientPath.close()

            expenseGradientPath.lineTo(lastX, bottomY)
            expenseGradientPath.close()

            // Draw subtle fills under curves
            drawPath(
                path = incomeGradientPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ElectricEmerald.copy(alpha = 0.22f),
                        ElectricEmerald.copy(alpha = 0.01f)
                    ),
                    startY = topY,
                    endY = bottomY
                )
            )

            drawPath(
                path = expenseGradientPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VividCoral.copy(alpha = 0.16f),
                        VividCoral.copy(alpha = 0.01f)
                    ),
                    startY = topY,
                    endY = bottomY
                )
            )

            // Draw Curve Strokes
            drawPath(
                path = incomePath,
                color = ElectricEmerald,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = expensePath,
                color = VividCoral,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw data points & selection indicators
            for (i in 0 until n) {
                val x = i * stepX
                val yInc = getY(dataPoints[i].income)
                val yExp = getY(dataPoints[i].expense)

                val isSelected = selectedPointIndex == i

                // Highlight selected column with vertical guideline
                if (isSelected) {
                    drawLine(
                        color = (if (extraColors.isDark) Color.White else Color.Black).copy(alpha = 0.3f),
                        start = Offset(x, topY),
                        end = Offset(x, bottomY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = dashEffect
                    )
                }

                // Income dot
                drawCircle(
                    color = ElectricEmerald,
                    radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                    center = Offset(x, yInc)
                )

                // Expense dot
                drawCircle(
                    color = VividCoral,
                    radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                    center = Offset(x, yExp)
                )
            }
        }

        // X-Axis Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dataPoints.forEachIndexed { index, point ->
                val isSelected = selectedPointIndex == index
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) ElectricEmerald else extraColors.textMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
