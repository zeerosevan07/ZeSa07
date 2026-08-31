package com.zesa07.security.ui.screens.permissionanalyzer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.domain.network.AppPermissionReport
import com.zesa07.security.domain.network.InstalledAppPermissionAuditor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class PermissionAnalyzerState(
    val reports: List<AppPermissionReport> = emptyList(),
    val isLoading: Boolean = false,
    val hasRun: Boolean = false
)

@HiltViewModel
class PermissionAnalyzerViewModel @Inject constructor(
    private val auditor: InstalledAppPermissionAuditor,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PermissionAnalyzerState())
    val state: StateFlow<PermissionAnalyzerState> = _state

    fun runAudit() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            val reports = withContext(Dispatchers.Default) { auditor.auditInstalledApps() }
            _state.value = _state.value.copy(reports = reports, isLoading = false, hasRun = true)
            progressRepository.recordPermissionAudit()
        }
    }
}
