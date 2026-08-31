package com.zesa07.security.ui.screens.networkdiscovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.data.repository.ScanHistoryRepository
import com.zesa07.security.domain.model.LabDevice
import com.zesa07.security.domain.network.LocalNetworkScanner
import com.zesa07.security.util.IpUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkDiscoveryState(
    val subnetInput: String = "192.168.1",
    val devices: List<LabDevice> = emptyList(),
    val isScanning: Boolean = false,
    val error: String? = null,
    val showConfirmDialog: Boolean = false
)

@HiltViewModel
class NetworkDiscoveryViewModel @Inject constructor(
    private val scanner: LocalNetworkScanner,
    private val scanHistoryRepository: ScanHistoryRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NetworkDiscoveryState())
    val state: StateFlow<NetworkDiscoveryState> = _state

    fun updateSubnet(value: String) {
        _state.value = _state.value.copy(subnetInput = value, error = null)
    }

    fun requestScanConfirmation() {
        if (!IpUtils.isAuthorizedLabSubnetBase(_state.value.subnetInput.trim())) {
            _state.value = _state.value.copy(
                error = "That subnet is not a private (RFC1918) range. ZeSa07 only scans your own local network, e.g. 192.168.1 or 10.0.0"
            )
            return
        }
        _state.value = _state.value.copy(showConfirmDialog = true)
    }

    fun dismissConfirmation() {
        _state.value = _state.value.copy(showConfirmDialog = false)
    }

    fun confirmAndScan() {
        val subnet = _state.value.subnetInput.trim()
        _state.value = _state.value.copy(showConfirmDialog = false, isScanning = true, error = null, devices = emptyList())
        viewModelScope.launch {
            try {
                val results = scanner.discoverDevices(subnet)
                _state.value = _state.value.copy(devices = results, isScanning = false)
                scanHistoryRepository.logScan(
                    scanType = "LAB_DISCOVERY",
                    target = "$subnet.0/24",
                    summary = "Authorized-lab discovery found ${results.size} responsive hosts",
                    resultCount = results.size
                )
                progressRepository.recordScanRun()
            } catch (e: IllegalArgumentException) {
                _state.value = _state.value.copy(isScanning = false, error = e.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isScanning = false, error = e.message ?: "Discovery failed")
            }
        }
    }
}
