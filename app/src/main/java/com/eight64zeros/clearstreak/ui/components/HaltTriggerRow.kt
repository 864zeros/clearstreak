package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.model.HaltTrigger
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HaltTriggerRow(
    selectedTrigger: HaltTrigger?,
    onSelectTrigger: (HaltTrigger) -> Unit,
    modifier: Modifier = Modifier
) {
    val triggers = listOf(
        HaltTrigger.HUNGRY,
        HaltTrigger.ANGRY,
        HaltTrigger.LONELY,
        HaltTrigger.TIRED,
        HaltTrigger.STRESSED,
        HaltTrigger.HOPELESS
    )

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        triggers.forEach { trigger ->
            val isSelected = selectedTrigger == trigger
            val bg = if (isSelected) OIASage.copy(alpha = 0.2f) else OIAWarmWhite
            val border = if (isSelected) BorderStroke(1.5.dp, OIASage) else BorderStroke(1.dp, OIATaupe.copy(alpha = 0.4f))

            Surface(
                modifier = Modifier
                    .height(44.dp)
                    .clickable { onSelectTrigger(trigger) },
                shape = RoundedCornerShape(12.dp),
                color = bg,
                border = border,
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = trigger.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trigger.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = OIACharcoal
                    )
                }
            }
        }
    }
}
