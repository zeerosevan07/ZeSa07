package com.zesa07.security.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zesa07.security.data.repository.ProgressRepository
import com.zesa07.security.data.repository.ScanHistoryRepository
import com.zesa07.security.domain.ctf.CtfChallengeCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class DashboardState(
    val totalScans: Int = 0,
    val flagsSolved: Int = 0,
    val totalFlags: Int = CtfChallengeCatalog.challenges.size,
    val totalPoints: Int = 0,
    val achievementsUnlocked: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val scanHistoryRepository: ScanHistoryRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        combine(
            scanHistoryRepository.observeLogs(),
            progressRepository.observeCtfProgress(),
            progressRepository.observeAchievements()
        ) { logs, ctf, achievements ->
            DashboardState(
                totalScans = logs.size,
                flagsSolved = ctf.count { it.solved },
                totalFlags = CtfChallengeCatalog.challenges.size,
                totalPoints = ctf.sumOf { it.pointsAwarded },
                achievementsUnlocked = achievements.count { it.unlocked }
            )
        }.onEach { _state.value = it }.launchIn(viewModelScope)
    }
}
