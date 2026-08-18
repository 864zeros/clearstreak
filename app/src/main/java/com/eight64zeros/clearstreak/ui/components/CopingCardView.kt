package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.model.CopingCard
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIADustyBlue
import com.eight64zeros.clearstreak.ui.theme.OIARadiusPill
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@Composable
fun CopingCardView(
    card: CopingCard,
    modifier: Modifier = Modifier,
    onToggleFavorite: ((Boolean) -> Unit)? = null
) {
    OIACard(
        modifier = modifier,
        backgroundColor = OIAWarmWhite,
        cornerRadius = 16.dp,
        padding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Badge
            Surface(
                shape = OIARadiusPill,
                color = OIASage.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${card.triggerCategory} • ${card.minUrgeLevel.displayName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OIASage,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            if (onToggleFavorite != null) {
                IconButton(onClick = { onToggleFavorite(!card.isFavorite) }) {
                    Icon(
                        imageVector = if (card.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (card.isFavorite) OIACoral else OIAStone
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Text
        Text(
            text = card.actionText,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIACharcoal,
            lineHeight = 22.sp
        )

        // Rationale / CBT Principle
        if (!card.rationale.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Why this works: ${card.rationale}",
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = OIAStone,
                lineHeight = 18.sp
            )
        }
    }
}
