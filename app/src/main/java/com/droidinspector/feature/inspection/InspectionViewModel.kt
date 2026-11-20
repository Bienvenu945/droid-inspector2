package com.droidinspector.feature.inspection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.AppPackageInfo
import com.droidinspector.domain.model.DeviceSummary
import com.droidinspector.domain.model.SensorInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {
    val device: DeviceSummary = repository.getDeviceSummary()
    val sensors: List<SensorInfo> = repository.getAvailableSensors()
    private val _apps = MutableStateFlow<List<AppPackageInfo>>(emptyList())
    val apps: StateFlow<List<AppPackageInfo>> = _apps.asStateFlow()
    private val _includeSystem = MutableStateFlow(false)
    val includeSystem: StateFlow<Boolean> = _includeSystem.asStateFlow()

    init { loadApps() }

    fun loadApps(includeSystemApps: Boolean = _includeSystem.value) {
        _includeSystem.value = includeSystemApps
        viewModelScope.launch { _apps.value = withContext(Dispatchers.IO) { repository.getInstalledApplications(includeSystemApps) } }
    }

    fun sensorUpdates(type: Int) = repository.getSensorUpdates(type)
}
