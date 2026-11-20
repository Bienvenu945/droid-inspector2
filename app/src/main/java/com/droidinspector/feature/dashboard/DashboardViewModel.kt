package com.droidinspector.feature.dashboard

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * État de l'écran Dashboard de Droid Inspector X.
 * Signature : @Bienvenu
 */
data class DashboardUiState(
    // Appareil
    val manufacturer: String = "",
    val model: String = "",

    // Système Android
    val androidVersion: String = "",
    val apiLevel: Int = 0,

    // Mémoire RAM
    val ramUsedBytes: Long = 0L,
    val ramTotalBytes: Long = 0L,
    val ramUsedFormatted: String = "0 Go",
    val ramTotalFormatted: String = "0 Go",
    val ramUsagePercentage: Float = 0f,

    // Stockage interne
    val storageUsedBytes: Long = 0L,
    val storageTotalBytes: Long = 0L,
    val storageUsedFormatted: String = "0 Go",
    val storageTotalFormatted: String = "0 Go",
    val storageUsagePercentage: Float = 0f,

    // Batterie
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val batteryTemperature: Float = 0f,
    val batteryStatusText: String = "",

    // Indicateur de chargement initial
    val isLoaded: Boolean = false
)

/**
 * ViewModel Hilt pour l'écran Dashboard avec rafraîchissement automatique toutes les 2 secondes.
 * Signature : @Bienvenu
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        startMetricsPolling()
    }

    private fun startMetricsPolling() {
        viewModelScope.launch {
            while (isActive) {
                updateMetrics()
                delay(2000L) // Rafraîchissement automatique toutes les 2 secondes
            }
        }
    }

    fun updateMetrics() {
        // 1. Informations de l'appareil
        val manufacturerRaw = Build.MANUFACTURER ?: "Inconnu"
        val manufacturer = manufacturerRaw.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
        val model = Build.MODEL ?: "Inconnu"

        // 2. Informations Android
        val androidVersion = Build.VERSION.RELEASE ?: "Inconnu"
        val apiLevel = Build.VERSION.SDK_INT

        // 3. Mémoire RAM via ActivityManager.MemoryInfo
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val ramTotal = memoryInfo.totalMem
        val ramAvail = memoryInfo.availMem
        val ramUsed = (ramTotal - ramAvail).coerceAtLeast(0L)
        val ramPercentage = if (ramTotal > 0L) {
            ((ramUsed.toDouble() / ramTotal.toDouble()) * 100).toFloat().coerceIn(0f, 100f)
        } else 0f

        // 4. Stockage interne via StatFs sur Environment.getDataDirectory()
        val dataDir = Environment.getDataDirectory()
        val statFs = StatFs(dataDir.path)
        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availBlocks = statFs.availableBlocksLong

        val storageTotal = totalBlocks * blockSize
        val storageAvail = availBlocks * blockSize
        val storageUsed = (storageTotal - storageAvail).coerceAtLeast(0L)
        val storagePercentage = if (storageTotal > 0L) {
            ((storageUsed.toDouble() / storageTotal.toDouble()) * 100).toFloat().coerceIn(0f, 100f)
        } else 0f

        // 5. Batterie via sticky broadcast Intent.ACTION_BATTERY_CHANGED (sans runtime permission)
        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, batteryFilter)

        val rawLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val rawScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (rawLevel >= 0 && rawScale > 0) {
            (rawLevel * 100) / rawScale
        } else {
            0
        }

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val batteryStatusText = when {
            status == BatteryManager.BATTERY_STATUS_FULL -> "Pleine"
            isCharging -> "En charge"
            status == BatteryManager.BATTERY_STATUS_DISCHARGING -> "En décharge"
            status == BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Non branché"
            else -> "Sur batterie"
        }

        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val batteryTempCelsius = rawTemp / 10f

        _uiState.value = DashboardUiState(
            manufacturer = manufacturer,
            model = model,
            androidVersion = androidVersion,
            apiLevel = apiLevel,
            ramUsedBytes = ramUsed,
            ramTotalBytes = ramTotal,
            ramUsedFormatted = formatGigabytes(ramUsed),
            ramTotalFormatted = formatGigabytes(ramTotal),
            ramUsagePercentage = ramPercentage,
            storageUsedBytes = storageUsed,
            storageTotalBytes = storageTotal,
            storageUsedFormatted = formatGigabytes(storageUsed),
            storageTotalFormatted = formatGigabytes(storageTotal),
            storageUsagePercentage = storagePercentage,
            batteryLevel = batteryPct,
            isCharging = isCharging,
            batteryTemperature = batteryTempCelsius,
            batteryStatusText = batteryStatusText,
            isLoaded = true
        )
    }

    private fun formatGigabytes(bytes: Long): String {
        val gb = bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
        return String.format(Locale.US, "%.1f Go", gb)
    }
}
