package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.model.Milestone
import com.eight64zeros.clearstreak.model.MilestoneTier
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIADustyBlue
import com.eight64zeros.clearstreak.ui.theme.OIAMustard
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIATaupe

/**
 * A ceremonial milestone "coin" — echoes the recovery-chip tradition, but generic to any journey
 * (not AA-specific). Deliberately *not* a score: each coin marks a milestone permanently earned.
 * Tier tints the metal; a soft vertical gradient + darker ring give a subtle embossed/3D feel.
 */
@Composable
fun MilestoneCoin(milestone: Milestone, modifier: Modifier = Modifier) {
    val base = when (milestone.tier) {
        MilestoneTier.DAILY -> OIASage
        MilestoneTier.WEEKLY -> OIADustyBlue
        MilestoneTier.MONTHLY -> OIATaupe
        MilestoneTier.YEARLY -> OIAMustard
    }
    val faceLight = lerp(base, Color.White, 0.40f)
    val ring = lerp(base, Color.Black, 0.18f)

    Column(
        modifier = modifier.width(74.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ring)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            // Inner face (gradient = subtle emboss)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(faceLight, base)))
                    .border(1.dp, ring.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = milestone.badge, fontSize = 26.sp)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = milestone.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIACharcoal,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}
