package com.zesa07.security.ui.screens.labs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.domain.ctf.LabExerciseCatalog
import com.zesa07.security.domain.model.LabExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LabsListState(
    val exercises: List<LabExercise> = LabExerciseCatalog.exercises,
    val completedIds: Set<String> = emptySet()
)

@HiltViewModel
class LabsListViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LabsListState())
    val state: StateFlow<LabsListState> = _state

    init {
        progressRepository.observeLabProgress()
            .onEach { list -> _state.value = _state.value.copy(completedIds = list.filter { it.completed }.map { it.labId }.toSet()) }
            .launchIn(viewModelScope)
    }
}

data class LabDetailState(
    val lab: LabExercise? = null,
    val simulatedMode: String = "vulnerable", // "vulnerable" | "secure" - toggled by the user in the sim
    val simulatedInput: String = "",
    val simulatedOutput: String = "",
    val completed: Boolean = false
)

@HiltViewModel
class LabDetailViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(LabDetailState())
    val state: StateFlow<LabDetailState> = _state

    init {
        val id = savedStateHandle.get<String>("labId")
        _state.value = _state.value.copy(lab = id?.let { LabExerciseCatalog.byId(it) })
        viewModelScope.launch {
            progressRepository.observeLabProgress().onEach { list ->
                val done = list.find { it.labId == id }?.completed == true
                _state.value = _state.value.copy(completed = done)
            }.launchIn(this)
        }
    }

    fun toggleMode() {
        _state.value = _state.value.copy(simulatedMode = if (_state.value.simulatedMode == "vulnerable") "secure" else "vulnerable")
    }

    /** Purely local, string-based simulation of the classic admin'-- SQLi bypass pattern - no real DB/query executes. */
    fun runSqliSimulation(input: String) {
        val bypassPattern = Regex("""^\s*'\s*(--|#)""")
        val looksLikeBypass = input.contains("'") && (input.contains("--") || input.contains("#") || input.contains(" OR ", true))
        val output = if (_state.value.simulatedMode == "vulnerable") {
            if (looksLikeBypass) {
                "SIMULATED backend query: SELECT * FROM users WHERE user='$input' AND pass='...'\n" +
                    "\u26a0\ufe0f The quote + comment token truncated the password check \u2014 login BYPASSED (simulated)."
            } else {
                "SIMULATED backend: login rejected (no bypass pattern detected)."
            }
        } else {
            "SIMULATED parameterized query: user bound as a literal parameter, not concatenated.\n" +
                "\u2705 Injection syntax is treated as plain text \u2014 login safely rejected."
        }
        _state.value = _state.value.copy(simulatedInput = input, simulatedOutput = output)
    }

    fun markComplete() {
        val lab = _state.value.lab ?: return
        viewModelScope.launch { progressRepository.markLabComplete(lab.id) }
    }
}
