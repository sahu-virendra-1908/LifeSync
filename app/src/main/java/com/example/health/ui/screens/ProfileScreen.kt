
package com.example.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.health.ui.theme.MutedGray
import com.example.health.ui.theme.PureWhite

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.material3.MaterialTheme.colorScheme.background
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Profile", color = PureWhite, fontSize = 30.sp)
        Text(
            "Your personal health settings.",
            color = MutedGray,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(12.dp))
        Text("HEALTH GOALS", color = MutedGray, fontSize = 12.sp)
        Text("Daily steps: 8,000", color = PureWhite, fontSize = 18.sp)
        Text("Water goal: 2.5 L", color = PureWhite, fontSize = 18.sp)
        Text(
            "Settings and editable goals will be added later.",
            color = MutedGray,
            fontSize = 13.sp
        )
    }
}
