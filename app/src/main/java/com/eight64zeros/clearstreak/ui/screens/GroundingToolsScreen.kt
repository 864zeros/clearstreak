package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.ui.components.BoxBreather
import com.eight64zeros.clearstreak.ui.components.MiniGamesCard
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.PocketAnchor
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroundingToolsScreen(
    gamesAllowed: Boolean,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Grounding & Reset",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OIACharcoal
                    )
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Eyes-free tools to ride out the wave. No screens to read — just follow the pulse.",
                fontSize = 14.sp,
                color = OIAStone
            )

            OIACard(
                backgroundColor = OIAWarmWhite,
                cornerRadius = 16.dp,
                padding = 20.dp
            ) {
                Text(
                    text = "🫁 Pocket Anchor",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(12.dp))
                PocketAnchor()
            }

            OIACard(
                backgroundColor = OIAWarmWhite,
                cornerRadius = 16.dp,
                padding = 20.dp
            ) {
                Text(
                    text = "🌬️ 4-4-4-4 Box Breather",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(16.dp))
                BoxBreather()
            }

            // Mini-games are hidden for gaming/screen-recovery journeys (blueprint §7)
            if (gamesAllowed) {
                MiniGamesCard()
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
