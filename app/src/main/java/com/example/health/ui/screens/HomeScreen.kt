
package com.example.health.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.health.sensors.StepCounterTracker
import com.example.health.ui.components.HealthCard
import com.example.health.ui.components.ProgressRing
import com.example.health.ui.theme.CardSurface
import com.example.health.ui.theme.MutedGray
import com.example.health.ui.theme.PureWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private const val STEP_GOAL = 8000
private const val WATER_GOAL_ML = 2500

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val today = LocalDate.now()
    val todayKey = "entries_$today"

    // Step tracking
    val tracker = remember {
        StepCounterTracker(context)
    }

    var steps by remember { mutableIntStateOf(0) }

    var sensorAvailable by remember {
        mutableStateOf(tracker.isAvailable())
    }

    var permissionGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < 29 ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            permissionGranted = granted
        }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 29 && !permissionGranted) {
            permissionLauncher.launch(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }

    DisposableEffect(tracker, permissionGranted) {
        if (permissionGranted) {
            sensorAvailable = tracker.start { newSteps ->
                steps = newSteps
            }
        }

        onDispose {
            tracker.stop()
        }
    }

    // Shared water storage: same preferences and key format as WaterScreen.
    val waterPreferences = remember {
        context.getSharedPreferences(
            "health_tracker_water",
            Context.MODE_PRIVATE
        )
    }

    var waterMl by remember(todayKey) {
        mutableIntStateOf(
            (waterPreferences.getString(todayKey, "") ?: "")
                .split(",")
                .mapNotNull { it.toIntOrNull() }
                .sum()
        )
    }

    val distanceKm = steps * 0.00075
    val calories = (steps * 0.04).roundToInt()
    val progress =
        (steps.toFloat() / STEP_GOAL).coerceIn(0f, 1f)
    val waterProgress =
        (waterMl.toFloat() / WATER_GOAL_ML).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "HEALTH / DAILY",
                        color = MutedGray,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        "Your wellbeing.",
                        color = PureWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        today.format(
                            DateTimeFormatter.ofPattern("EEEE, dd MMMM")
                        ),
                        color = MutedGray,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(CardSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("H", color = PureWhite, fontSize = 18.sp)
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    "DAILY ACTIVITY",
                    color = MutedGray,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "%,d".format(steps),
                            color = PureWhite,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            when {
                                !sensorAvailable ->
                                    "Step sensor unavailable"
                                !permissionGranted ->
                                    "Activity permission required"
                                else -> "steps recorded"
                            },
                            color = MutedGray,
                            fontSize = 13.sp
                        )

                        Spacer(Modifier.height(14.dp))

                        Text(
                            "Goal: %,d steps".format(STEP_GOAL),
                            color = PureWhite,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "%,d steps to go".format(
                                (STEP_GOAL - steps).coerceAtLeast(0)
                            ),
                            color = MutedGray,
                            fontSize = 11.sp
                        )

                        if (!permissionGranted && Build.VERSION.SDK_INT >= 29) {
                            TextButton(
                                onClick = {
                                    permissionLauncher.launch(
                                        Manifest.permission.ACTIVITY_RECOGNITION
                                    )
                                }
                            ) {
                                Text("Grant permission")
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                    ProgressRing(progress = progress)
                }
            }
        }

        item {
            Text(
                "YOUR METRICS",
                color = MutedGray,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HealthCard(
                    title = "Distance",
                    symbol = "↗",
                    value = "%.2f".format(distanceKm),
                    unit = "km",
                    subtitle = "Estimated from steps",
                    modifier = Modifier.weight(1f)
                )

                HealthCard(
                    title = "Calories",
                    symbol = "◇",
                    value = "%,d".format(calories),
                    unit = "kcal",
                    subtitle = "Rough estimate",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HealthCard(
                    title = "Water",
                    symbol = "◉",
                    value = "%.2f".format(waterMl / 1000.0),
                    unit = "L",
                    subtitle = "of 2.5 L goal",
                    modifier = Modifier.weight(1f)
                )

                HealthCard(
                    title = "Step goal",
                    symbol = "◎",
                    value = "${(progress * 100).roundToInt()}",
                    unit = "%",
                    subtitle = "Daily completion",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "WATER INTAKE",
                        color = MutedGray,
                        fontSize = 11.sp
                    )

                    Text(
                        "${(waterProgress * 100).roundToInt()}%",
                        color = PureWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .background(
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(waterProgress)
                            .fillMaxHeight()
                            .background(PureWhite, RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    if (waterMl >= WATER_GOAL_ML) {
                        "Daily water goal reached!"
                    } else {
                        "${WATER_GOAL_ML - waterMl} ml remaining"
                    },
                    color = MutedGray,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    // Append to the same daily entry list as WaterScreen.
                    val updatedEntries =
                        (waterPreferences.getString(todayKey, "") ?: "")
                            .split(",")
                            .filter { it.isNotBlank() }
                            .toMutableList()

                    updatedEntries.add("250")

                    waterPreferences.edit()
                        .putString(todayKey, updatedEntries.joinToString(","))
                        .apply()

                    waterMl = updatedEntries.sumOf {
                        it.toIntOrNull() ?: 0
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add 250 ml water")
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Text(
                    "ACTIVITY STATUS",
                    color = MutedGray,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    when {
                        !sensorAvailable ->
                            "This phone cannot provide step-counter data."
                        !permissionGranted ->
                            "Grant activity permission to track steps."
                        steps >= STEP_GOAL ->
                            "Daily step goal completed."
                        steps == 0 ->
                            "Start walking to record your steps."
                        else ->
                            "Keep moving. You're making progress."
                    },
                    color = PureWhite,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
