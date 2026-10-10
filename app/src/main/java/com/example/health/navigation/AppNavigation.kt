
package com.example.health.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.health.ui.screens.ActivityScreen
import com.example.health.ui.screens.HomeScreen
import com.example.health.ui.screens.InsightsScreen
import com.example.health.ui.screens.ProfileScreen
import com.example.health.ui.screens.WaterScreen
import com.example.health.ui.theme.MutedGray
import com.example.health.ui.theme.PureWhite

private enum class AppTab(
    val title: String,
    val symbol: String
) {
    HOME("Home", "⌂"),
    ACTIVITY("Activity", "↗"),
    WATER("Water", "◉"),
    INSIGHTS("Insights", "▥"),
    PROFILE("Profile", "○")
}

@Composable
fun HealthApp() {
    var selectedTab by rememberSaveable {
        mutableStateOf(AppTab.HOME.name)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTab.entries.forEach { tab ->
                    val selected = selectedTab == tab.name

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTab = tab.name
                            }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = tab.symbol,
                            color = if (selected) PureWhite else MutedGray,
                            fontSize = 22.sp
                        )

                        Text(
                            text = tab.title,
                            color = if (selected) PureWhite else MutedGray,
                            fontSize = 10.sp,
                            fontWeight = if (selected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (AppTab.valueOf(selectedTab)) {
                AppTab.HOME -> HomeScreen()
                AppTab.ACTIVITY -> ActivityScreen()
                AppTab.WATER -> WaterScreen()
                AppTab.INSIGHTS -> InsightsScreen()
                AppTab.PROFILE -> ProfileScreen()
            }
        }
    }
}
