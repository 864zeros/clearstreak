package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.navigation.Screen
import com.eight64zeros.clearstreak.ui.components.ClearStreakBottomBar
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACoral
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIARadiusPill
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    journalEntries: List<CheckIn>,
    journeys: List<Journey>,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val journeyMap = journeys.associateBy { it.id }
    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Encrypted Journal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = OIACharcoal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = OIASage,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OIACharcoal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OIACream)
            )
        },
        bottomBar = {
            ClearStreakBottomBar(current = Screen.Journal.route, onNavigate = onNavigate)
        }
    ) { paddingValues ->
        if (journalEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No check-ins yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OIACharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Every check-in appears here, encrypted — with any reflection you write.",
                        fontSize = 14.sp,
                        color = OIAStone,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(journalEntries) { entry ->
                    val journey = journeyMap[entry.journeyId]
                    OIACard(
                        backgroundColor = OIAWarmWhite,
                        cornerRadius = 16.dp,
                        padding = 16.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = journey?.title ?: "Journey",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OIASage
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = OIARadiusPill,
                                    color = if (entry.isSlip) OIACoral.copy(alpha = 0.2f) else OIASage.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (entry.isSlip) "Slip Logged" else "${entry.urgeLevel.emoji} ${entry.urgeLevel.displayName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (entry.isSlip) OIACoral else OIASage,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = dateFormat.format(Date(entry.timestamp * 1000)),
                            fontSize = 12.sp,
                            color = OIATaupe
                        )

                        entry.haltTrigger?.let { halt ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Trigger: ${halt.emoji} ${halt.displayName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = OIAStone
                            )
                        }

                        if (!entry.noteEncrypted.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = entry.noteEncrypted,
                                fontSize = 15.sp,
                                color = OIACharcoal,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
