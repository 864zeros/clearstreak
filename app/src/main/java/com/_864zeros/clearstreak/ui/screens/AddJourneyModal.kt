package com._864zeros.clearstreak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com._864zeros.clearstreak.model.Journey
import com._864zeros.clearstreak.model.JourneyCategory
import com._864zeros.clearstreak.ui.components.OIAPrimaryButton
import com._864zeros.clearstreak.ui.components.OIATextField
import com._864zeros.clearstreak.ui.theme.OIACharcoal
import com._864zeros.clearstreak.ui.theme.OIACream
import com._864zeros.clearstreak.ui.theme.OIASage
import com._864zeros.clearstreak.ui.theme.OIAStone
import com._864zeros.clearstreak.ui.theme.OIATaupe
import com._864zeros.clearstreak.ui.theme.OIAWarmWhite

@Composable
fun AddJourneyModal(
    onAddJourney: (Journey) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(JourneyCategory.SUBSTANCE) }
    var dailyCostSavingsStr by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OIACream)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Recovery Target",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "What habit or substance are you leaving behind?",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIACharcoal
            )

            Spacer(modifier = Modifier.height(8.dp))

            OIATextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "e.g., Alcohol, Smoking, Gambling, Social Media"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Category",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIACharcoal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JourneyCategory.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) OIASage.copy(alpha = 0.2f) else OIAWarmWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) OIASage else OIATaupe.copy(alpha = 0.4f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = cat.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = OIACharcoal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Daily Cost Saved ($ / day, optional)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIACharcoal
            )

            Spacer(modifier = Modifier.height(8.dp))

            OIATextField(
                value = dailyCostSavingsStr,
                onValueChange = { dailyCostSavingsStr = it },
                placeholder = "e.g., 15.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OIAPrimaryButton(
                text = "Start Journey",
                enabled = title.isNotBlank(),
                onClick = {
                    val savings = dailyCostSavingsStr.toDoubleOrNull() ?: 0.0
                    val journey = Journey(
                        title = title.trim(),
                        category = selectedCategory,
                        startTimestamp = System.currentTimeMillis() / 1000,
                        dailyCostSavings = savings
                    )
                    onAddJourney(journey)
                }
            )
        }
    }
}
