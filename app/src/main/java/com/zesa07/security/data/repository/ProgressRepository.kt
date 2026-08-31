package com.zesa07.security.data.repository

import com.zesa07.security.data.db.dao.AchievementDao
import com.zesa07.security.data.db.dao.CtfProgressDao
import com.zesa07.security.data.db.dao.LabProgressDao
import com.zesa07.security.data.db.entities.AchievementEntity
import com.zesa07.security.data.db.entities.CtfProgressEntity
import com.zesa07.security.data.db.entities.LabProgressEntity
import com.zesa07.security.domain.ctf.CtfChallengeCatalog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

data class AchievementDef(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val target: Int
)

@Singleton
class ProgressRepository @Inject constructor(
    private val ctfDao: CtfProgressDao,
    private val labDao: LabProgressDao,
    private val achievementDao: AchievementDao
) {
    val achievementDefs = listOf(
        AchievementDef("first_scan", "First Steps", "Run your first authorized lab scan", "radar", 1),
        AchievementDef("first_flag", "Flag Hunter", "Solve your first CTF challenge", "flag", 1),
        AchievementDef("all_flags", "CTF Champion", "Solve every bundled CTF challenge", "trophy", CtfChallengeCatalog.challenges.size),
        AchievementDef("hasher", "Cryptographer", "Use the hashing toolkit 5 times", "lock", 5),
        AchievementDef("lab_complete", "Lab Rat", "Complete a vulnerable-lab exercise", "flask", 1),
        AchievementDef("permission_audit", "Privacy Auditor", "Run the permission analyzer", "shield", 1)
    )

    fun observeCtfProgress(): Flow<List<CtfProgressEntity>> = ctfDao.observeAll()
    fun observeLabProgress(): Flow<List<LabProgressEntity>> = labDao.observeAll()
    fun observeAchievements(): Flow<List<AchievementEntity>> = achievementDao.observeAll()

    suspend fun recordCtfAttempt(challengeId: String, correct: Boolean, points: Int) {
        val existing = ctfDao.get(challengeId)
        if (existing?.solved == true) return // already solved, don't overwrite

        ctfDao.upsert(
            CtfProgressEntity(
                challengeId = challengeId,
                solved = correct,
                attempts = (existing?.attempts ?: 0) + 1,
                pointsAwarded = if (correct) points else 0,
                solvedAtMillis = if (correct) System.currentTimeMillis() else null
            )
        )
        if (correct) {
            incrementAchievement("first_flag")
            setAchievementProgress("all_flags", currentValue = countSolvedFlags())
        }
    }

    suspend fun markLabComplete(labId: String) {
        labDao.upsert(LabProgressEntity(labId = labId, completed = true, completedAtMillis = System.currentTimeMillis()))
        incrementAchievement("lab_complete")
    }

    suspend fun recordScanRun() = incrementAchievement("first_scan")

    suspend fun recordHashUse() = incrementAchievement("hasher")

    suspend fun recordPermissionAudit() = incrementAchievement("permission_audit")

    /** Bumps a counter-style achievement by 1 and unlocks it once its target is reached. */
    private suspend fun incrementAchievement(id: String) {
        val def = achievementDefs.find { it.id == id } ?: return
        val existing = achievementDao.get(id)
        val currentCount = ((existing?.progress ?: 0f) * def.target).toInt()
        setAchievementProgress(id, currentCount + 1)
    }

    private suspend fun setAchievementProgress(id: String, currentValue: Int) {
        val def = achievementDefs.find { it.id == id } ?: return
        val clamped = currentValue.coerceAtMost(def.target)
        val ratio = if (def.target == 0) 1f else clamped / def.target.toFloat()
        val existing = achievementDao.get(id)
        val alreadyUnlocked = existing?.unlocked == true
        achievementDao.upsert(
            AchievementEntity(
                achievementId = id,
                unlocked = alreadyUnlocked || ratio >= 1f,
                progress = ratio,
                unlockedAtMillis = if (!alreadyUnlocked && ratio >= 1f) System.currentTimeMillis()
                    else existing?.unlockedAtMillis
            )
        )
    }

    private suspend fun countSolvedFlags(): Int =
        CtfChallengeCatalog.challenges.count { challenge -> ctfDao.get(challenge.id)?.solved == true }
}
