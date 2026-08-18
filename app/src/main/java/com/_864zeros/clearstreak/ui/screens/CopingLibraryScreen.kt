package com._864zeros.clearstreak.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com._864zeros.clearstreak.model.CopingCard
import com._864zeros.clearstreak.ui.components.CopingCardView
import com._864zeros.clearstreak.ui.theme.OIACharcoal
import com._864zeros.clearstreak.ui.theme.OIACoral
import com._864zeros.clearstreak.ui.theme.OIACream
import com._864zeros.clearstreak.ui.theme.OIASage
import com._864zeros.clearstreak.ui.theme.OIAStone
import com._864zeros.clearstreak.ui.theme.OIATaupe
import com._864zeros.clearstreak.ui.theme.OIAWarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopingLibraryScreen(
    cards: List<CopingCard>,
    onToggleFavorite: (String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var favoritesOnly by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "HUNGRY", "ANGRY", "LONELY", "TIRED", "STRESSED", "HOPELESS", "GENERAL")

    val filteredCards = cards.filter { card ->
        val matchesCat = selectedCategory == "ALL" || card.triggerCategory.equals(selectedCategory, ignoreCase = true)
        val matchesFav = !favoritesOnly || card.isFavorite
        matchesCat && matchesFav
    }

    Scaffold(
        containerColor = OIACream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Coping Strategies (${cards.size})",
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
        ) {
            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = favoritesOnly,
                    onClick = { favoritesOnly = !favoritesOnly },
                    label = { Text("❤️ Favorites") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OIACoral.copy(alpha = 0.2f),
                        selectedLabelColor = OIACoral
                    )
                )

                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OIASage.copy(alpha = 0.2f),
                            selectedLabelColor = OIASage
                        )
                    )
                }
            }

            // Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredCards) { card ->
                    CopingCardView(
                        card = card,
                        onToggleFavorite = { isFav -> onToggleFavorite(card.id, isFav) }
                    )
                }
            }
        }
    }
}
