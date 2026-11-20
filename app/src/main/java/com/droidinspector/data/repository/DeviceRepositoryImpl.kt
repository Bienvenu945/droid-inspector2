package com.droidinspector.data.repository

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.Display
import com.droidinspector.domain.model.*
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {

    override fun getDeviceSummary(): DeviceSummary {
        return DeviceSummary(
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A",
            buildNumber = Build.DISPLAY,
            board = Build.BOARD,
            bootloader = Build.BOOTLOADER,
            radioVersion = Build.getRadioVersion() ?: "Unknown"
        )
    }

    override fun getBatteryInfo(): Flow<BatteryInfo> = callbackFlow {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null) return

                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val percentage = if (level >= 0 && scale > 0) (level * 100) / scale else 0

                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                val source = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                    BatteryManager.BATTERY_PLUGGED_AC -> "Secteur (AC)"
                    BatteryManager.BATTERY_PLUGGED_USB -> "Port USB"
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Sans fil (induction)"
                    else -> if (isCharging) "En charge" else "Batterie"
                }

                val health = when (
                    intent.getIntExtra(
                        BatteryManager.EXTRA_HEALTH,
                        BatteryManager.BATTERY_HEALTH_UNKNOWN
                    )
                ) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> "Bonne"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Surchauffe"
                    BatteryManager.BATTERY_HEALTH_DEAD -> "Défectueuse"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Surtension"
                    BatteryManager.BATTERY_HEALTH_COLD -> "Trop froide"
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Défaut non précisé"
                    else -> "Inconnue"
                }

                val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                    ?.takeIf { it.isNotBlank() } ?: "Non précisée"
                val temperatureCelsius = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

                // Vendor dependent: the system returns Long.MIN_VALUE (or 0) when unsupported.
                val currentNow = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                val chargeCounter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                val cycleCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1)
                } else {
                    -1
                }

                trySend(
                    BatteryInfo(
                        percentage = percentage,
                        isCharging = isCharging,
                        chargingSource = source,
                        health = health,
                        technology = technology,
                        temperatureCelsius = temperatureCelsius,
                        voltageMilliVolts = voltage,
                        currentNowMicroAmps = currentNow.takeIf { it != Long.MIN_VALUE },
                        chargeCounterMicroAmpHours = chargeCounter.takeIf { it > 0L },
                        cycleCount = cycleCount.takeIf { it > 0 }
                    )
                )
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    @Suppress("DEPRECATION")
    override fun getHardwareInfo(): HardwareInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display: Display? = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val metrics = DisplayMetrics()
        var refreshRate = 0f
        if (display != null) {
            display.getRealMetrics(metrics)
            refreshRate = display.refreshRate
        } else {
            metrics.setTo(context.resources.displayMetrics)
        }

        val coreCount = Runtime.getRuntime().availableProcessors()
        val cores = (0 until coreCount).map { index ->
            CpuCore(
                index = index,
                minFrequencyMhz = readCpuFrequencyMhz(index, "cpuinfo_min_freq"),
                maxFrequencyMhz = readCpuFrequencyMhz(index, "cpuinfo_max_freq")
            )
        }

        return HardwareInfo(
            socName = socName(),
            cpuArchitecture = System.getProperty("os.arch") ?: Build.SUPPORTED_ABIS.firstOrNull() ?: "Inconnue",
            abiList = Build.SUPPORTED_ABIS.toList(),
            coreCount = coreCount,
            cores = cores,
            ramTotalBytes = memInfo.totalMem,
            ramAvailableBytes = memInfo.availMem,
            swapTotalBytes = readMeminfoKb("SwapTotal")?.times(1024L),
            swapFreeBytes = readMeminfoKb("SwapFree")?.times(1024L),
            screenResolution = "${metrics.widthPixels} x ${metrics.heightPixels}",
            screenDensityDpi = metrics.densityDpi,
            screenRefreshRateHz = refreshRate,
            openGlEsVersion = activityManager.deviceConfigurationInfo.glEsVersion
        )
    }

    override fun getAvailableSensors(): List<SensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)

        return sensorList.map { sensor ->
            val typeStr = when (sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> "Accéléromètre"
                Sensor.TYPE_GYROSCOPE -> "Gyroscope"
                Sensor.TYPE_MAGNETIC_FIELD -> "Magnétomètre"
                Sensor.TYPE_LIGHT -> "Luminosité"
                Sensor.TYPE_PROXIMITY -> "Proximité"
                Sensor.TYPE_PRESSURE -> "Baromètre"
                Sensor.TYPE_GRAVITY -> "Gravité"
                Sensor.TYPE_LINEAR_ACCELERATION -> "Accélération linéaire"
                Sensor.TYPE_ROTATION_VECTOR -> "Vecteur de rotation"
                Sensor.TYPE_STEP_COUNTER -> "Compteur de pas"
                else -> sensor.stringType ?: "Autre capteur"
            }

            SensorInfo(
                id = sensor.type,
                name = sensor.name,
                type = sensor.type,
                typeString = typeStr,
                vendor = sensor.vendor,
                version = sensor.version,
                maxRange = sensor.maximumRange,
                resolution = sensor.resolution,
                powerMa = sensor.power
            )
        }
    }

    override fun getSensorUpdates(sensorType: Int): Flow<List<Float>> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(sensorType)

        if (sensor == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let { vals ->
                    trySend(vals.toList())
                }
            }

            override fun onAccuracyChanged(s: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    override fun getStorageInfo(): StorageInfo {
        val volumes = mutableListOf<StorageVolumeInfo>()

        val data = statVolume("Données (/data)", Environment.getDataDirectory())
        if (data != null) volumes.add(data)

        val system = statVolume("Système (/system)", Environment.getRootDirectory())
        if (system != null) volumes.add(system)

        // Entries after the first one are removable volumes (SD card, USB drive).
        context.getExternalFilesDirs(null)
            .drop(1)
            .filterNotNull()
            .forEachIndexed { index, directory ->
                val volume = statVolume("Carte SD / USB ${index + 1}", directory)
                if (volume != null) volumes.add(volume)
            }

        val total = data?.totalBytes ?: 0L
        val available = data?.availableBytes ?: 0L
        return StorageInfo(
            totalBytes = total,
            availableBytes = available,
            usedBytes = total - available,
            volumes = volumes
        )
    }

    override suspend fun getInstalledApplications(includeSystemApps: Boolean): List<AppPackageInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

        return packages.mapNotNull { pkg ->
            val isSys = (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
            if (!includeSystemApps && isSys) return@mapNotNull null

            val appName = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName
            val permissions = pkg.requestedPermissions?.toList() ?: emptyList()

            @Suppress("DEPRECATION")
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkg.longVersionCode
            } else {
                pkg.versionCode.toLong()
            }

            AppPackageInfo(
                appName = appName,
                packageName = pkg.packageName,
                versionName = pkg.versionName ?: "1.0",
                versionCode = versionCode,
                isSystemApp = isSys,
                firstInstallTime = pkg.firstInstallTime,
                lastUpdateTime = pkg.lastUpdateTime,
                targetSdkVersion = pkg.applicationInfo?.targetSdkVersion ?: 34,
                requestedPermissions = permissions
            )
        }
    }

    private fun statVolume(label: String, directory: File): StorageVolumeInfo? {
        return try {
            val stat = StatFs(directory.path)
            StorageVolumeInfo(
                label = label,
                path = directory.path,
                totalBytes = stat.totalBytes,
                availableBytes = stat.availableBytes
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun socName(): String {
        val parts = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (Build.SOC_MANUFACTURER.isNotBlank() && Build.SOC_MANUFACTURER != Build.UNKNOWN) {
                parts.add(Build.SOC_MANUFACTURER)
            }
            if (Build.SOC_MODEL.isNotBlank() && Build.SOC_MODEL != Build.UNKNOWN) {
                parts.add(Build.SOC_MODEL)
            }
        }
        if (parts.isEmpty()) {
            parts.add(Build.HARDWARE)
        }
        return parts.joinToString(" ")
    }

    /** Reads a cpufreq file (in kHz) and returns MHz, or null if the kernel hides it from apps. */
    private fun readCpuFrequencyMhz(core: Int, fileName: String): Long? {
        return try {
            File("/sys/devices/system/cpu/cpu$core/cpufreq/$fileName")
                .readText()
                .trim()
                .toLongOrNull()
                ?.div(1000L)
        } catch (e: Exception) {
            null
        }
    }

    /** Reads one /proc/meminfo entry (in kB), or null if unavailable. */
    private fun readMeminfoKb(key: String): Long? {
        return try {
            File("/proc/meminfo").useLines { lines ->
                lines.firstOrNull { it.startsWith("$key:") }
                    ?.substringAfter(":")
                    ?.trim()
                    ?.substringBefore(" ")
                    ?.toLongOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }
}
