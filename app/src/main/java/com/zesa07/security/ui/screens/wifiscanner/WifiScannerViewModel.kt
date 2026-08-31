package com.zesa07.security.ui.screens.wifiscanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ScanHistoryRepository
import com.zesa07.security.domain.model.WifiNetworkInfo
import com.zesa07.security.domain.wifi.WifiInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class WifiScannerState(
    val networks: List<WifiNetworkInfo> = emptyList(),
    val isScanning: Boolean = false,
    val lastError: String? = null,
    val showConfirmDialog: Boolean = false
)

@HiltViewModel
class WifiScannerViewModel @Inject constructor(
    private val wifiInfoProvider: WifiInfoProvider,
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WifiScannerState())
    val state: StateFlow<WifiScannerState> = _state

    fun requestScanConfirmation() {
        _state.value = _state.value.copy(showConfirmDialog = true)
    }

    fun dismissConfirmation() {
        _state.value = _state.value.copy(showConfirmDialog = false)
    }

    /** Called only after the user confirms via ConfirmScanDialog and permissions are granted. */
    fun confirmAndScan() {
        _state.value = _state.value.copy(showConfirmDialog = false, isScanning = true, lastError = null)
        viewModelScope.launch {
            try {
                wifiInfoProvider.requestScan()
                // Android delivers scan results asynchronously via a broadcast in real apps;
                // for a simple, dependency-light UI we re-read current results after a short delay.
                kotlinx.coroutines.delay(1200)
                val results = wifiInfoProvider.getScanResults()
                _state.value = _state.value.copy(networks = results, isScanning = false)
                scanHistoryRepository.logScan(
                    scanType = "WIFI_INFO",
                    target = "nearby-networks",
                    summary = "Wi-Fi info scan found ${results.size} nearby networks",
                    resultCount = results.size
                )
            } catch (e: SecurityException) {
                _state.value = _state.value.copy(isScanning = false, lastError = "Location permission is required for Wi-Fi scan results.")
            } catch (e: Exception) {
                _state.value = _state.value.copy(isScanning = false, lastError = e.message ?: "Scan failed")
            }
        }
    }

    fun securityAdvice(security: String): String = wifiInfoProvider.securityAdvice(security)
}
