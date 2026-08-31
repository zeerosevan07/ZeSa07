package com.zesa07.security.ui.screens.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.db.entities.TutorMessageEntity
import com.zesa07.security.data.repository.TutorRepository
import com.zesa07.security.domain.tutor.TutorTurn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TutorUiState(
    val messages: List<TutorMessageEntity> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val hasApiKey: Boolean = false
)

@HiltViewModel
class TutorViewModel @Inject constructor(
    private val repository: TutorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TutorUiState())
    val state: StateFlow<TutorUiState> = _state

    init {
        _state.value = _state.value.copy(hasApiKey = repository.hasApiKey())
        repository.observeMessages().onEach { msgs ->
            _state.value = _state.value.copy(messages = msgs)
        }.launchIn(viewModelScope)
    }

    fun refreshApiKeyStatus() {
        _state.value = _state.value.copy(hasApiKey = repository.hasApiKey())
    }

    fun updateInput(value: String) {
        _state.value = _state.value.copy(input = value)
    }

    fun send() {
        val text = _state.value.input.trim()
        if (text.isEmpty()) return
        if (!repository.hasApiKey()) {
            _state.value = _state.value.copy(error = "Add your Anthropic API key in Settings to chat with the tutor.")
            return
        }
        _state.value = _state.value.copy(isSending = true, input = "", error = null)
        viewModelScope.launch {
            val history = _state.value.messages.map { TutorTurn(it.role, it.content) }
            val result = repository.sendMessage(text, history)
            result.onFailure { e -> _state.value = _state.value.copy(error = e.message) }
            _state.value = _state.value.copy(isSending = false)
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}
