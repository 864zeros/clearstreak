package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.model.JourneyCategory
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.OIATextField
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddJourneyModal(
    onAddJourney: (Journey) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(JourneyCategory.ALCOHOL) }
    var customLabel by remember { mutableStateOf("") }
    var dailyCostSavingsStr by remember { mutableStateOf("") }
    var startMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val startDateLabel = remember(startMillis) {
        java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(startMillis))
    }

    if (showDatePicker) {
        val todayMs = System.currentTimeMillis()
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = startMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMs
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { startMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dpState)
        }
    }

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

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JourneyCategory.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) OIASage.copy(alpha = 0.2f) else OIAWarmWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) OIASage else OIATaupe.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            text = "${cat.emoji} ${cat.displayName}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = OIACharcoal
                        )
                    }
                }
            }

            if (selectedCategory == JourneyCategory.BEHAVIORAL || selectedCategory == JourneyCategory.CUSTOM) {
                Spacer(modifier = Modifier.height(12.dp))
                OIATextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    placeholder = "Label this (e.g., Social media, Shopping)"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Start date (when did you begin?)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = OIACharcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                color = OIAWarmWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, OIATaupe.copy(alpha = 0.4f))
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = "📅  $startDateLabel",
                        modifier = Modifier.padding(horizontal = 14.dp),
                        fontSize = 15.sp,
                        color = OIACharcoal
                    )
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
                        customLabel = customLabel.trim().ifBlank { null },
                        startTimestamp = startMillis / 1000,
                        dailyCostSavings = savings
                    )
                    onAddJourney(journey)
                }
            )
        }
    }
}
