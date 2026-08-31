package com.zesa07.security.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_logs")
data class ScanLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMillis: Long,
    val scanType: String,       // WIFI_INFO, LAB_DISCOVERY, LAB_PORT_SCAN, PERMISSION_AUDIT, MALWARE_SIM
    val target: String,         // e.g. subnet base or "current-network" - never a real 3rd-party host
    val summary: String,
    val resultCount: Int
)

@Entity(tableName = "ctf_progress")
data class CtfProgressEntity(
    @PrimaryKey val challengeId: String,
    val solved: Boolean,
    val attempts: Int,
    val pointsAwarded: Int,
    val solvedAtMillis: Long?
)

@Entity(tableName = "lab_progress")
data class LabProgressEntity(
    @PrimaryKey val labId: String,
    val completed: Boolean,
    val completedAtMillis: Long?,
    val notes: String? = null
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val unlocked: Boolean,
    val progress: Float,
    val unlockedAtMillis: Long?
)

@Entity(tableName = "tutor_messages")
data class TutorMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMillis: Long,
    val role: String, // "user" | "assistant"
    val content: String
)
