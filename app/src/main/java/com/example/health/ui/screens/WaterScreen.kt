
package com.example.health.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.health.ui.theme.CardSurface
import com.example.health.ui.theme.MutedGray
import com.example.health.ui.theme.PureWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val WATER_GOAL_ML = 2500

@Composable
fun WaterScreen() {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("health_tracker_water", Context.MODE_PRIVATE)
    }

    val today = LocalDate.now()
    val todayKey = today.toString()
    val storageKey = "entries_$todayKey"

    val entries = remember(todayKey) {
        mutableStateListOf<Int>().apply {
            val saved = prefs.getString(storageKey, "") ?: ""
            if (saved.isNotBlank()) {
                saved.split(",").mapNotNull { it.toIntOrNull() }
                    .forEach { add(it) }
            }
        }
    }

    var customAmount by remember { mutableStateOf("") }
    val totalMl = entries.sum()
    val progress = (totalMl.toFloat() / WATER_GOAL_ML).coerceIn(0f, 1f)

    fun saveEntries() {
        prefs.edit()
            .putString(storageKey, entries.joinToString(","))
            .apply()
    }

    fun addWater(amount: Int) {
        if (amount <= 0 || amount > 5000) return
        entries.add(amount)
        saveEntries()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(Modifier.height(18.dp))

            Text(
                "HEALTH / HYDRATION",
                color = MutedGray,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Stay hydrated.",
                color = PureWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM")),
                color = MutedGray,
                fontSize = 13.sp
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(24.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("TODAY'S INTAKE", color = MutedGray, fontSize = 11.sp)

                Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                    Text(
                        "%.2f".format(totalMl / 1000.0),
                        color = PureWhite,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("L / 2.5 L", color = MutedGray, fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(PureWhite, RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    if (totalMl >= WATER_GOAL_ML) {
                        "Daily goal reached!"
                    } else {
                        "${WATER_GOAL_ML - totalMl} ml remaining to reach your goal"
                    },
                    color = MutedGray,
                    fontSize = 13.sp
                )
            }
        }

        item {
            Text(
                "ADD WATER",
                color = MutedGray,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(150, 250, 500).forEach { amount ->
                    Button(
                        onClick = { addWater(amount) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+${amount}ml")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = customAmount,
                onValueChange = { input ->
                    customAmount = input.filter { it.isDigit() }.take(4)
                },
                label = { Text("Custom amount (ml)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val amount = customAmount.toIntOrNull()
                    if (amount != null && amount in 1..5000) {
                        addWater(amount)
                        customAmount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = (customAmount.toIntOrNull() ?: 0) in 1..5000
            ) {
                Text("Add custom amount")
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "TODAY'S ENTRIES",
                    color = MutedGray,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )

                TextButton(
                    onClick = {
                        if (entries.isNotEmpty()) {
                            entries.removeAt(entries.lastIndex)
                            saveEntries()
                        }
                    },
                    enabled = entries.isNotEmpty()
                ) {
                    Text("Undo last")
                }
            }

            if (entries.isEmpty()) {
                Text(
                    "No water logged yet. Add your first drink above.",
                    color = MutedGray,
                    fontSize = 14.sp
                )
            } else {
                entries.asReversed().forEachIndexed { index, amount ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Water entry ${entries.size - index}",
                            color = PureWhite
                        )
                        Text("+$amount ml", color = PureWhite)
                    }
                }
            }
        }

        item {
            Text(
                "LAST 7 DAYS",
                color = MutedGray,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )
        }

        items(7) { index ->
            val date = today.minusDays(index.toLong())
            val key = "entries_$date"
            val saved = if (index == 0) {
                entries.toList()
            } else {
                (prefs.getString(key, "") ?: "")
                    .split(",")
                    .mapNotNull { it.toIntOrNull() }
            }
            val dayTotal = saved.sum()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        if (index == 0) "Today"
                        else date.format(DateTimeFormatter.ofPattern("EEE, dd MMM")),
                        color = PureWhite,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "${saved.size} entries",
                        color = MutedGray,
                        fontSize = 12.sp
                    )
                }

                Text(
                    "%.2f L".format(dayTotal / 1000.0),
                    color = PureWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}
