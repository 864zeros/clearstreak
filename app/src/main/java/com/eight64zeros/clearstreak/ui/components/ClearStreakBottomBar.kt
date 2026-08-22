package com.eight64zeros.clearstreak.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eight64zeros.clearstreak.navigation.Screen
import com.eight64zeros.clearstreak.ui.theme.OIASage
import com.eight64zeros.clearstreak.ui.theme.OIAStone
import com.eight64zeros.clearstreak.ui.theme.OIATaupe
import com.eight64zeros.clearstreak.ui.theme.OIAWarmWhite

/**
 * The omni-present bottom navigation, shared across the five primary areas
 * (Home · Journal · Reset · Reflect · Settings). [current] is the active route;
 * [onNavigate] routes to a tapped destination (no-op when already there).
 */
@Composable
fun ClearStreakBottomBar(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = OIAWarmWhite, tonalElevation = 4.dp) {
        item(current, Screen.Dashboard.route, Icons.Default.FlashOn, "Home", onNavigate)
        item(current, Screen.Journal.route, Icons.Outlined.Book, "Journal", onNavigate)
        item(current, Screen.Reset.route, Icons.Outlined.Spa, "Reset", onNavigate)
        item(current, Screen.Heritage.route, Icons.Outlined.MenuBook, "Reflect", onNavigate)
        item(current, Screen.Settings.route, Icons.Outlined.Settings, "Settings", onNavigate)
    }
}

@Composable
private fun RowScope.item(
    current: String,
    route: String,
    icon: ImageVector,
    label: String,
    onNavigate: (String) -> Unit
) {
    val selected = current == route
    NavigationBarItem(
        selected = selected,
        onClick = { if (!selected) onNavigate(route) },
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 12.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = OIASage,
            selectedTextColor = OIASage,
            indicatorColor = OIASage.copy(alpha = 0.15f),
            unselectedIconColor = OIATaupe,
            unselectedTextColor = OIAStone
        )
    )
}
