package com.zesa07.security.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.zesa07.security.data.repository.TutorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsState(
    val apiKeyInput: String = "",
    val hasSavedKey: Boolean = false,
    val savedMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(hasSavedKey = tutorRepository.hasApiKey()))
    val state: StateFlow<SettingsState> = _state

    fun updateInput(value: String) {
        _state.value = _state.value.copy(apiKeyInput = value, savedMessage = null)
    }

    fun saveKey() {
        if (_state.value.apiKeyInput.isBlank()) return
        tutorRepository.saveApiKey(_state.value.apiKeyInput.trim())
        _state.value = SettingsState(hasSavedKey = true, savedMessage = "API key saved securely on-device.")
    }

    fun clearKey() {
        tutorRepository.clearApiKey()
        _state.value = SettingsState(hasSavedKey = false, savedMessage = "API key removed.")
    }
}
