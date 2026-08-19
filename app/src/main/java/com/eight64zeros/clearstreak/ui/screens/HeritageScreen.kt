package com.eight64zeros.clearstreak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.eight64zeros.clearstreak.model.DailyVerse
import com.eight64zeros.clearstreak.ui.components.OIACard
import com.eight64zeros.clearstreak.ui.components.OIAPrimaryButton
import com.eight64zeros.clearstreak.ui.components.VerseCalendar
import com.eight64zeros.clearstreak.ui.theme.OIACharcoal
import com.eight64zeros.clearstreak.ui.theme.OIACream
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val store = remember { HeritageStore(context) }
    val today = remember { LocalDate.now() }
    val dateFmt = remember { DateTimeFormatter.ofPattern("MMMM d") }

    var selectedDate by remember { mutableStateOf(today) }
    val dateVerse = remember(selectedDate, store) { store.verseForDate(selectedDate) }
    var randomVerse by remember { mutableStateOf<DailyVerse?>(null) }

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
            // 1) Serenity Prayer
            OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
                Text("The Serenity Prayer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = HeritageStore.SERENITY_PRAYER,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    color = OIACharcoal,
                    lineHeight = 24.sp
                )
            }

            // 2) Verse window (defaults to today)
            VerseCard(
                title = if (selectedDate == today) "Verse of the Day" else "Verse for ${selectedDate.format(dateFmt)}",
                verse = dateVerse
            )

            // 3) Verse calendar — tapping a date drives the card above
            OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 16.dp) {
                Text("Browse by Date", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
                Text(
                    text = "Tap any day to read its verse above.",
                    fontSize = 12.sp,
                    color = OIAStone
                )
                Spacer(modifier = Modifier.height(12.dp))
                VerseCalendar(
                    selectedDate = selectedDate,
                    onSelectDate = { selectedDate = it }
                )
            }

            // 4) Randomize
            OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
                Text("Random Verse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
                Spacer(modifier = Modifier.height(12.dp))
                OIAPrimaryButton(
                    text = "🎲 Give me a verse",
                    onClick = { randomVerse = store.randomVerse() }
                )
                randomVerse?.let { v ->
                    Spacer(modifier = Modifier.height(16.dp))
                    VerseBody(v)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VerseCard(title: String, verse: DailyVerse?) {
    OIACard(backgroundColor = OIAWarmWhite, cornerRadius = 16.dp, padding = 20.dp) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OIACharcoal)
        Spacer(modifier = Modifier.height(12.dp))
        if (verse == null) {
            Text("Verse content is unavailable.", fontSize = 13.sp, color = OIAStone)
        } else {
            VerseBody(verse)
        }
    }
}

@Composable
private fun VerseBody(verse: DailyVerse) {
    Text(
        text = "“${verse.text}”",
        fontSize = 17.sp,
        fontStyle = FontStyle.Italic,
        color = OIACharcoal,
        lineHeight = 25.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "— ${verse.citation}",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = OIASage
    )
}
