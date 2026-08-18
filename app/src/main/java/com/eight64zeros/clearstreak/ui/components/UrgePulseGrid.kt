package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.model.UrgeLevel
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import com.eight64zeros.clearstreak.ui.theme.UrgeClearGreen
import com.eight64zeros.clearstreak.ui.theme.UrgeCriticalRed
import com.eight64zeros.clearstreak.ui.theme.UrgeKnucklingOrange
import com.eight64zeros.clearstreak.ui.theme.UrgePassingYellow

@Composable
fun UrgePulseGrid(
    selectedUrge: UrgeLevel?,
    onSelectUrge: (UrgeLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UrgeGridTile(
                level = UrgeLevel.CLEAR,
                tint = UrgeClearGreen,
                isSelected = selectedUrge == UrgeLevel.CLEAR,
                modifier = Modifier.weight(1f),
                onClick = { onSelectUrge(UrgeLevel.CLEAR) }
            )
            UrgeGridTile(
                level = UrgeLevel.PASSING,
                tint = UrgePassingYellow,
                isSelected = selectedUrge == UrgeLevel.PASSING,
                modifier = Modifier.weight(1f),
                onClick = { onSelectUrge(UrgeLevel.PASSING) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UrgeGridTile(
                level = UrgeLevel.WHITE_KNUCKLING,
                tint = UrgeKnucklingOrange,
                isSelected = selectedUrge == UrgeLevel.WHITE_KNUCKLING,
                modifier = Modifier.weight(1f),
                onClick = { onSelectUrge(UrgeLevel.WHITE_KNUCKLING) }
            )
            UrgeGridTile(
                level = UrgeLevel.CRITICAL,
                tint = UrgeCriticalRed,
                isSelected = selectedUrge == UrgeLevel.CRITICAL,
                modifier = Modifier.weight(1f),
                onClick = { onSelectUrge(UrgeLevel.CRITICAL) }
            )
        }
    }
}

@Composable
private fun UrgeGridTile(
    level: UrgeLevel,
    tint: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) tint.copy(alpha = 0.20f) else OIAWarmWhite
    val border = if (isSelected) BorderStroke(2.dp, tint) else BorderStroke(1.dp, OIATaupe.copy(alpha = 0.3f))

    Card(
        modifier = modifier
            .height(84.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = level.emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = level.displayName,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = OIACharcoal
                )
            }
        }
    }
}
