package com.eight64zeros.clearstreak.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIAMustard
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

/**
 * Reusable support/review card for 864zeros mobile apps (Android; a SwiftUI twin mirrors it).
 * Three elements under a variable [quote]: a 5-star review affordance, and an expandable
 * "how it works" radical-transparency statement.
 *
 * Compliance: tapping the stars launches the OS review flow regardless of which star is pressed —
 * we never capture the value or route by sentiment (no review-gating, no incentive).
 */
@Composable
fun SupportCard(
    quote: String,
    onRateClicked: () -> Unit,
    modifier: Modifier = Modifier,
    transparencyTitle: String = "How this works",
    transparencyBody: String =
        "No ads, no tracking, and we never see or sell your data. Honest ratings and word of mouth " +
        "are the only way we grow — a moment of your time helps someone else find this."
) {
    var showHow by remember { mutableStateOf(false) }

    OIACard(
        modifier = modifier,
        backgroundColor = OIAWarmWhite,
        cornerRadius = 16.dp,
        padding = 18.dp
    ) {
        Text(
            text = quote,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium,
            color = OIACharcoal,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5-star review affordance — whole row launches the review flow.
        Row(
            modifier = Modifier.clickable { onRateClicked() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rate",
                    tint = OIAMustard,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Rate on Google Play",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIASage
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Radical-transparency statement (tap to expand).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showHow = !showHow },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = transparencyTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIAStone
            )
            Icon(
                imageVector = if (showHow) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (showHow) "Collapse" else "Expand",
                tint = OIAStone,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = showHow) {
            Text(
                text = transparencyBody,
                fontSize = 13.sp,
                color = OIAStone,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
