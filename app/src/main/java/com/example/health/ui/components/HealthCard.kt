
package com.example.health.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.health.ui.theme.BorderGray
import com.example.health.ui.theme.CardSurface
import com.example.health.ui.theme.MutedGray
import com.example.health.ui.theme.PureWhite

@Composable
fun HealthCard(
    title: String,
    symbol: String,
    value: String,
    unit: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(CardSurface, RoundedCornerShape(20.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = MutedGray, fontSize = 13.sp)
            Text(symbol, color = PureWhite, fontSize = 17.sp)
        }

        Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
            Text(
                text = value,
                color = PureWhite,
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = " $unit",
                color = MutedGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Text(subtitle, color = MutedGray, fontSize = 11.sp)
    }
}
