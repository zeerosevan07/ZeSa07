package com.zesa07.security.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zesa07.security.data.db.dao.AchievementDao
import com.zesa07.security.data.db.dao.CtfProgressDao
import com.zesa07.security.data.db.dao.LabProgressDao
import com.zesa07.security.data.db.dao.ScanLogDao
import com.zesa07.security.data.db.dao.TutorMessageDao
import com.zesa07.security.data.db.entities.AchievementEntity
import com.zesa07.security.data.db.entities.CtfProgressEntity
import com.zesa07.security.data.db.entities.LabProgressEntity
import com.zesa07.security.data.db.entities.ScanLogEntity
import com.zesa07.security.data.db.entities.TutorMessageEntity

@Database(
    entities = [
        ScanLogEntity::class,
        CtfProgressEntity::class,
        LabProgressEntity::class,
        AchievementEntity::class,
        TutorMessageEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanLogDao(): ScanLogDao
    abstract fun ctfProgressDao(): CtfProgressDao
    abstract fun labProgressDao(): LabProgressDao
    abstract fun achievementDao(): AchievementDao
    abstract fun tutorMessageDao(): TutorMessageDao

    companion object {
        const val DB_NAME = "zesa07.db"
    }
}
