
package com.example.health.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.time.LocalDate
import kotlin.math.roundToInt

class StepCounterTracker(context: Context) {

    private val appContext = context.applicationContext

    private val sensorManager =
        appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val preferences = appContext.getSharedPreferences(
        "health_tracker_steps",
        Context.MODE_PRIVATE
    )

    private var listener: SensorEventListener? = null

    fun start(onStepsUpdated: (Int) -> Unit): Boolean {
        val sensor = stepSensor ?: return false

        stop()

        val today = LocalDate.now().toString()
        val baselineKey = "baseline_$today"
        var baseline = preferences.getFloat(baselineKey, -1f)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val totalSteps = event?.values?.firstOrNull() ?: return

                // Initialize the baseline or handle a sensor count reset.
                if (baseline < 0f || totalSteps < baseline) {
                    baseline = totalSteps
                    preferences.edit()
                        .putFloat(baselineKey, baseline)
                        .apply()
                }

                val todaySteps = (totalSteps - baseline)
                    .roundToInt()
                    .coerceAtLeast(0)

                onStepsUpdated(todaySteps)
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) = Unit
        }

        listener = sensorListener

        return sensorManager.registerListener(
            sensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        listener?.let { sensorManager.unregisterListener(it) }
        listener = null
    }

    fun isAvailable(): Boolean = stepSensor != null
}
