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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.data.HeritageStore
import com.eight64zeros.clearstreak.model.Verse
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIATextField
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val store = remember { HeritageStore(context) }
    val today = remember { LocalDate.now().dayOfMonth }
    val todaysVerses = remember { store.proverbsForChapter(today) }

    var query by remember { mutableStateOf("") }
    val results = remember(query) { if (query.isBlank()) emptyList() else store.search(query) }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Faith & Heritage",
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
            // Serenity Prayer
            OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
                Text(
                    text = "The Serenity Prayer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = HeritageStore.SERENITY_PRAYER,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    color = OIACharcoal,
                    lineHeight = 24.sp
                )
            }

            // Proverb of the day (day_of_month -> chapter)
            OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
                Text(
                    text = "Proverbs · Chapter $today",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Text(
                    text = "Today's chapter (day $today of the month)",
                    fontSize = 12.sp,
                    color = OIAStone
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (todaysVerses.isEmpty()) {
                    Text(
                        text = "Full text pending. Add the public-domain WEB Proverbs to assets/proverbs_web.txt to complete the vault.",
                        fontSize = 13.sp,
                        color = OIAStone
                    )
                } else {
                    todaysVerses.forEach { VerseRow(it) }
                }
            }

            // Search
            OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
                Text(
                    text = "Search Proverbs",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OIACharcoal
                )
                Spacer(modifier = Modifier.height(10.dp))
                OIATextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Search for a word or phrase…"
                )
                if (query.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (results.isEmpty()) {
                        Text(text = "No matches.", fontSize = 13.sp, color = OIAStone)
                    } else {
                        results.forEach { VerseRow(it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VerseRow(verse: Verse) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = "Proverbs ${verse.chapter}:${verse.verse}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OIASage
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = verse.text,
            fontSize = 15.sp,
            color = OIACharcoal,
            lineHeight = 22.sp
        )
    }
}
