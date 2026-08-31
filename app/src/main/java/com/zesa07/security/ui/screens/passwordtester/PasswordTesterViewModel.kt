package com.zesa07.security.ui.screens.passwordtester

import androidx.lifecycle.ViewModel
import com.zesa07.security.domain.model.PasswordAnalysis
import com.zesa07.security.domain.password.PasswordStrengthEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class PasswordTesterState(
    val password: String = "",
    val analysis: PasswordAnalysis? = null
)

/**
 * NOTE: the raw password string lives only in in-memory Compose/ViewModel state for this
 * screen's lifetime. It is never written to Room, DataStore, logs, or any network call -
 * see ScanHistoryRepository usage (absent here on purpose).
 */
@HiltViewModel
class PasswordTesterViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PasswordTesterState())
    val state: StateFlow<PasswordTesterState> = _state

    fun updatePassword(value: String) {
        _state.value = PasswordTesterState(password = value, analysis = PasswordStrengthEngine.analyze(value))
    }

    fun generateTestPassword() {
        val generated = PasswordStrengthEngine.generateTestPassword()
        updatePassword(generated)
    }
}
