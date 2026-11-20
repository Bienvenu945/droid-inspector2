package com.droidinspector.feature.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidinspector.domain.model.HardwareInfo
import com.droidinspector.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HardwareInfo?>(null)
    val uiState: StateFlow<HardwareInfo?> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        // sysfs / procfs reads happen off the main thread.
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = repository.getHardwareInfo()
        }
    }
}
