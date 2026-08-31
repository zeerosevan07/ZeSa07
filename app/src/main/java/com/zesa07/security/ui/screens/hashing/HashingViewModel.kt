package com.zesa07.security.ui.screens.hashing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.domain.crypto.AesDemoResult
import com.zesa07.security.domain.crypto.HashingEngine
import com.zesa07.security.domain.model.HashAlgorithm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HashingState(
    val input: String = "",
    val hashes: Map<HashAlgorithm, String> = emptyMap(),
    val salt: String = "",
    val saltedHash: String = "",
    val aesResult: AesDemoResult? = null
)

@HiltViewModel
class HashingViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HashingState())
    val state: StateFlow<HashingState> = _state

    fun updateInput(value: String) {
        val hashes = if (value.isNotEmpty()) HashingEngine.hashAll(value) else emptyMap()
        _state.value = _state.value.copy(input = value, hashes = hashes)
        if (value.isNotEmpty()) recordUse()
    }

    fun generateSaltDemo() {
        val salt = HashingEngine.randomSalt()
        val salted = if (_state.value.input.isNotEmpty()) HashingEngine.saltedSha256(_state.value.input, salt) else ""
        _state.value = _state.value.copy(salt = salt, saltedHash = salted)
    }

    fun runAesDemo() {
        if (_state.value.input.isEmpty()) return
        val result = HashingEngine.aesGcmDemo(_state.value.input)
        _state.value = _state.value.copy(aesResult = result)
    }

    private fun recordUse() {
        viewModelScope.launch { progressRepository.recordHashUse() }
    }
}
