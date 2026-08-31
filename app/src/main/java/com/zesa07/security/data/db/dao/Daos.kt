package com.zesa07.security.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zesa07.security.data.db.entities.AchievementEntity
import com.zesa07.security.data.db.entities.CtfProgressEntity
import com.zesa07.security.data.db.entities.LabProgressEntity
import com.zesa07.security.data.db.entities.ScanLogEntity
import com.zesa07.security.data.db.entities.TutorMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanLogDao {
    @Insert
    suspend fun insert(log: ScanLogEntity): Long

    @Query("SELECT * FROM scan_logs ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<ScanLogEntity>>

    @Query("DELETE FROM scan_logs")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM scan_logs WHERE scanType = :type")
    suspend fun countByType(type: String): Int
}

@Dao
interface CtfProgressDao {
    @Query("SELECT * FROM ctf_progress")
    fun observeAll(): Flow<List<CtfProgressEntity>>

    @Query("SELECT * FROM ctf_progress WHERE challengeId = :id")
    suspend fun get(id: String): CtfProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CtfProgressEntity)
}

@Dao
interface LabProgressDao {
    @Query("SELECT * FROM lab_progress")
    fun observeAll(): Flow<List<LabProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LabProgressEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun observeAll(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE achievementId = :id")
    suspend fun get(id: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AchievementEntity)

    @Update
    suspend fun update(entity: AchievementEntity)
}

@Dao
interface TutorMessageDao {
    @Query("SELECT * FROM tutor_messages ORDER BY timestampMillis ASC")
    fun observeAll(): Flow<List<TutorMessageEntity>>

    @Insert
    suspend fun insert(message: TutorMessageEntity): Long

    @Query("DELETE FROM tutor_messages")
    suspend fun clearAll()
}
