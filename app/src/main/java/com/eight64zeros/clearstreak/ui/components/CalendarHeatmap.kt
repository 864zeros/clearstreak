package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.UrgeLevel
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIAMustard
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/**
 * Derived per-day state for the minimalist heatmap (blueprint §4). The richer
 * 4-tier urge model + HALT is preserved in the check-in data; the calendar
 * collapses each day to one of three non-shaming states by priority.
 */
enum class DayState { NONE, CLEAR, URGE_OVERCOME, SLIP }

private fun dayStateFor(dayCheckIns: List<CheckIn>): DayState = when {
    dayCheckIns.isEmpty() -> DayState.NONE
    dayCheckIns.any { it.isSlip } -> DayState.SLIP
    dayCheckIns.any { it.urgeLevel != UrgeLevel.CLEAR } -> DayState.URGE_OVERCOME
    else -> DayState.CLEAR
}

@Composable
fun CalendarHeatmap(
    checkIns: List<CheckIn>,
    modifier: Modifier = Modifier
) {
    val zone = remember { ZoneId.systemDefault() }
    val statesByDate = remember(checkIns) {
        checkIns
            .groupBy { Instant.ofEpochSecond(it.timestamp).atZone(zone).toLocalDate() }
            .mapValues { (_, list) -> dayStateFor(list) }
    }

    val today = remember { LocalDate.now() }
    var month by remember { mutableStateOf(YearMonth.from(today)) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { month = month.minusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous month", tint = OIAStone)
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIACharcoal
            )
            IconButton(onClick = { if (month.isBefore(YearMonth.from(today))) month = month.plusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next month", tint = OIAStone)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday labels (Sunday-first)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                Text(
                    text = d,
                    fontSize = 11.sp,
                    color = OIATaupe,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Day grid
        val leadingBlanks = month.atDay(1).dayOfWeek.value % 7 // Sunday = 0
        val daysInMonth = month.lengthOfMonth()
        val rows = (leadingBlanks + daysInMonth + 6) / 7
        var dayCounter = 1

        for (r in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (c in 0 until 7) {
                    val cellIndex = r * 7 + c
                    if (cellIndex < leadingBlanks || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val date = month.atDay(dayCounter)
                        DayCell(
                            day = dayCounter,
                            state = statesByDate[date] ?: DayState.NONE,
                            isToday = date == today,
                            isFuture = date.isAfter(today),
                            modifier = Modifier.weight(1f)
                        )
                        dayCounter++
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LegendDot(OIASage, "Clear")
            LegendDot(OIAMustard, "Urge overcome")
            LegendDot(OIAWarmWhite, "Slip · new start", border = OIACoral)
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    state: DayState,
    isToday: Boolean,
    isFuture: Boolean,
    modifier: Modifier = Modifier
) {
    val (fill, textColor, borderColor) = when (state) {
        DayState.CLEAR -> Triple(OIASage, OIAWarmWhite, null)
        DayState.URGE_OVERCOME -> Triple(OIAMustard, OIACharcoal, null)
        DayState.SLIP -> Triple(OIAWarmWhite, OIACoral, OIACoral)
        DayState.NONE -> Triple(Color.Transparent, if (isFuture) OIATaupe.copy(alpha = 0.5f) else OIAStone, null)
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(fill)
                .then(if (borderColor != null) Modifier.border(1.5.dp, borderColor, CircleShape) else Modifier)
                .then(if (isToday) Modifier.border(1.5.dp, OIASage, CircleShape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$day",
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String, border: Color? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
                .then(if (border != null) Modifier.border(1.5.dp, border, CircleShape) else Modifier)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = OIAStone)
    }
}
