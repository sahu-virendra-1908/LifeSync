
package com.example.health.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.health.ui.theme.BorderGray
import com.example.health.ui.theme.PureWhite

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String = "TODAY"
) {
    val safeProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(132.dp)) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2
            val diameter = size.width - stroke

            drawCircle(
                color = BorderGray,
                radius = diameter / 2,
                center = Offset(size.width / 2, size.height / 2),
                style = Stroke(stroke)
            )

            drawArc(
                color = PureWhite,
                startAngle = -90f,
                sweepAngle = 360f * safeProgress,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(safeProgress * 100).toInt()}%",
            color = Color.White,
            fontSize = 25.sp
        )
    }
}
