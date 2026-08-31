package com.zesa07.security.data.repository

import com.zesa07.security.data.db.dao.ScanLogDao
import com.zesa07.security.data.db.entities.ScanLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanHistoryRepository @Inject constructor(
    private val dao: ScanLogDao
) {
    fun observeLogs(): Flow<List<ScanLogEntity>> = dao.observeAll()

    suspend fun logScan(scanType: String, target: String, summary: String, resultCount: Int) {
        dao.insert(
            ScanLogEntity(
                timestampMillis = System.currentTimeMillis(),
                scanType = scanType,
                target = target,
                summary = summary,
                resultCount = resultCount
            )
        )
    }

    suspend fun clear() = dao.clearAll()

    suspend fun countByType(type: String): Int = dao.countByType(type)
}
