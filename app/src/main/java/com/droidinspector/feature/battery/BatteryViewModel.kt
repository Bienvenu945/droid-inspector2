package com.droidinspector.feature.battery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.BatteryInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    repository: DeviceRepository
) : ViewModel() {

    /** Live readings; null until the first broadcast arrives. */
    val uiState: StateFlow<BatteryInfo?> = repository.getBatteryInfo()
        .stateIn<BatteryInfo?>(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
