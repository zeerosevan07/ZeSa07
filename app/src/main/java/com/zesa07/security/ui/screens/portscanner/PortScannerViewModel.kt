package com.zesa07.security.ui.screens.portscanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.data.repository.ScanHistoryRepository
import com.zesa07.security.domain.model.LabPortResult
import com.zesa07.security.domain.network.LabPortScanner
import com.zesa07.security.util.IpUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortScannerState(
    val ipInput: String = "192.168.1.1",
    val results: List<LabPortResult> = emptyList(),
    val isScanning: Boolean = false,
    val error: String? = null,
    val showConfirmDialog: Boolean = false
)

@HiltViewModel
class PortScannerViewModel @Inject constructor(
    private val scanner: LabPortScanner,
    private val scanHistoryRepository: ScanHistoryRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortScannerState())
    val state: StateFlow<PortScannerState> = _state

    fun updateIp(value: String) {
        _state.value = _state.value.copy(ipInput = value, error = null)
    }

    fun requestScanConfirmation() {
        val ip = _state.value.ipInput.trim()
        if (!IpUtils.isValidIpv4(ip) || !IpUtils.isAuthorizedLabAddress(ip)) {
            _state.value = _state.value.copy(error = "Enter a valid private-network IP you own (e.g. 192.168.1.1). Public IPs are refused.")
            return
        }
        _state.value = _state.value.copy(showConfirmDialog = true)
    }

    fun dismissConfirmation() {
        _state.value = _state.value.copy(showConfirmDialog = false)
    }

    fun confirmAndScan() {
        val ip = _state.value.ipInput.trim()
        _state.value = _state.value.copy(showConfirmDialog = false, isScanning = true, error = null, results = emptyList())
        viewModelScope.launch {
            try {
                val results = scanner.scanPorts(ip)
                _state.value = _state.value.copy(results = results, isScanning = false)
                val openCount = results.count { it.open }
                scanHistoryRepository.logScan(
                    scanType = "LAB_PORT_SCAN",
                    target = ip,
                    summary = "Port scan on authorized lab host found $openCount open of ${results.size} checked",
                    resultCount = openCount
                )
                progressRepository.recordScanRun()
            } catch (e: IllegalArgumentException) {
                _state.value = _state.value.copy(isScanning = false, error = e.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isScanning = false, error = e.message ?: "Scan failed")
            }
        }
    }
}
