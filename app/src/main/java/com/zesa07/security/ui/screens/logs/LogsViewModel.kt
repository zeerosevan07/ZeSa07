package com.zesa07.security.ui.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.db.entities.ScanLogEntity
import com.zesa07.security.data.repository.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val repository: ScanHistoryRepository
) : ViewModel() {
    private val _logs = MutableStateFlow<List<ScanLogEntity>>(emptyList())
    val logs: StateFlow<List<ScanLogEntity>> = _logs

    init {
        repository.observeLogs().onEach { _logs.value = it }.launchIn(viewModelScope)
    }

    fun clearLogs() {
        viewModelScope.launch { repository.clear() }
    }
}
