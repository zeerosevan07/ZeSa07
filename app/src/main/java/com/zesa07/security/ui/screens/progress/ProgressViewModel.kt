package com.zesa07.security.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.AchievementDef
import com.zesa07.security.data.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class AchievementUi(val def: AchievementDef, val unlocked: Boolean, val progress: Float)

data class ProgressUiState(
    val achievements: List<AchievementUi> = emptyList(),
    val totalPoints: Int = 0,
    val flagsSolved: Int = 0,
    val totalFlags: Int = 0,
    val labsCompleted: Int = 0,
    val totalLabs: Int = 0
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state

    init {
        combine(
            repository.observeAchievements(),
            repository.observeCtfProgress(),
            repository.observeLabProgress()
        ) { achievements, ctf, labs ->
            val achievementMap = achievements.associateBy { it.achievementId }
            ProgressUiState(
                achievements = repository.achievementDefs.map { def ->
                    val entity = achievementMap[def.id]
                    AchievementUi(def, entity?.unlocked == true, entity?.progress ?: 0f)
                },
                totalPoints = ctf.sumOf { it.pointsAwarded },
                flagsSolved = ctf.count { it.solved },
                totalFlags = com.zesa07.security.domain.ctf.CtfChallengeCatalog.challenges.size,
                labsCompleted = labs.count { it.completed },
                totalLabs = com.zesa07.security.domain.ctf.LabExerciseCatalog.exercises.size
            )
        }.onEach { _state.value = it }.launchIn(viewModelScope)
    }
}
