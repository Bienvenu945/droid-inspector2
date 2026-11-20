package com.droidinspector.domain.repository

import com.droidinspector.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDeviceSummary(): DeviceSummary
    fun getBatteryInfo(): Flow<BatteryInfo>
    fun getHardwareInfo(): HardwareInfo
    fun getAvailableSensors(): List<SensorInfo>
    fun getSensorUpdates(sensorType: Int): Flow<List<Float>>
    fun getStorageInfo(): StorageInfo
    suspend fun getInstalledApplications(includeSystemApps: Boolean = false): List<AppPackageInfo>
}
