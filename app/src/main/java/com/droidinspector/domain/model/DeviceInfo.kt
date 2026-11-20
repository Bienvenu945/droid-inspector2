package com.droidinspector.domain.model

data class DeviceSummary(
    val deviceName: String,
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val buildNumber: String,
    val board: String,
    val bootloader: String,
    val radioVersion: String
)

/** Real battery readings. A null value means the phone does not expose it. */
data class BatteryInfo(
    val percentage: Int,
    val isCharging: Boolean,
    val chargingSource: String,
    val health: String,
    val technology: String,
    val temperatureCelsius: Float,
    val voltageMilliVolts: Int,
    val currentNowMicroAmps: Long? = null,
    val chargeCounterMicroAmpHours: Long? = null,
    val cycleCount: Int? = null
)

/** One CPU core. Frequencies are null when the kernel does not let apps read them. */
data class CpuCore(
    val index: Int,
    val minFrequencyMhz: Long?,
    val maxFrequencyMhz: Long?
)

data class HardwareInfo(
    val socName: String,
    val cpuArchitecture: String,
    val abiList: List<String>,
    val coreCount: Int,
    val cores: List<CpuCore>,
    val ramTotalBytes: Long,
    val ramAvailableBytes: Long,
    val swapTotalBytes: Long?,
    val swapFreeBytes: Long?,
    val screenResolution: String,
    val screenDensityDpi: Int,
    val screenRefreshRateHz: Float,
    val openGlEsVersion: String
)

data class SensorInfo(
    val id: Int,
    val name: String,
    val type: Int,
    val typeString: String,
    val vendor: String,
    val version: Int,
    val maxRange: Float,
    val resolution: Float,
    val powerMa: Float,
    val currentValues: List<Float> = emptyList()
)

data class StorageVolumeInfo(
    val label: String,
    val path: String,
    val totalBytes: Long,
    val availableBytes: Long
) {
    val usedBytes: Long get() = totalBytes - availableBytes
}

data class StorageInfo(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
    val volumes: List<StorageVolumeInfo> = emptyList()
)

data class AppPackageInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val targetSdkVersion: Int,
    val requestedPermissions: List<String> = emptyList()
)
