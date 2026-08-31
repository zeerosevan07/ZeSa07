package com.zesa07.security.ui.screens.ctf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.db.entities.CtfProgressEntity
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.domain.ctf.CtfChallengeCatalog
import com.zesa07.security.domain.model.CtfChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CtfListState(
    val challenges: List<CtfChallenge> = CtfChallengeCatalog.challenges,
    val progress: Map<String, CtfProgressEntity> = emptyMap()
)

@HiltViewModel
class CtfListViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CtfListState())
    val state: StateFlow<CtfListState> = _state

    init {
        progressRepository.observeCtfProgress()
            .onEach { list -> _state.value = _state.value.copy(progress = list.associateBy { it.challengeId }) }
            .launchIn(viewModelScope)
    }
}

data class CtfDetailState(
    val challenge: CtfChallenge? = null,
    val submission: String = "",
    val result: Boolean? = null,
    val alreadySolved: Boolean = false,
    val revealedHints: Int = 0
)

@HiltViewModel
class CtfDetailViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CtfDetailState())
    val state: StateFlow<CtfDetailState> = _state

    init {
        val id = savedStateHandle.get<String>("challengeId")
        val challenge = id?.let { CtfChallengeCatalog.byId(it) }
        _state.value = _state.value.copy(challenge = challenge)
        viewModelScope.launch {
            progressRepository.observeCtfProgress().onEach { list ->
                val solved = list.find { it.challengeId == id }?.solved == true
                _state.value = _state.value.copy(alreadySolved = solved)
            }.launchIn(this)
        }
    }

    fun updateSubmission(value: String) {
        _state.value = _state.value.copy(submission = value, result = null)
    }

    fun revealHint() {
        val challenge = _state.value.challenge ?: return
        val next = (_state.value.revealedHints + 1).coerceAtMost(challenge.hints.size)
        _state.value = _state.value.copy(revealedHints = next)
    }

    fun submitFlag() {
        val challenge = _state.value.challenge ?: return
        val correct = CtfChallengeCatalog.checkFlag(challenge, _state.value.submission)
        _state.value = _state.value.copy(result = correct)
        viewModelScope.launch {
            progressRepository.recordCtfAttempt(challenge.id, correct, challenge.points)
        }
    }
}
