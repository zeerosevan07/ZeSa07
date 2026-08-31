package com.zesa07.security.domain.network

import android.content.Context
import android.content.pm.PackageManager
import com.zesa07.security.domain.model.PermissionCatalog
import com.zesa07.security.domain.model.PermissionFinding
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class AppPermissionReport(
    val packageName: String,
    val appLabel: String,
    val findings: List<PermissionFinding>
)

/**
 * Reads permission metadata for apps installed ON THE USER'S OWN DEVICE via the standard
 * [PackageManager] APIs (the same read-only introspection any launcher uses) and explains the
 * risk of each requested permission. This is local device hygiene analysis, not a network
 * attack or a way to access another app's data/sandbox.
 */
@Singleton
class InstalledAppPermissionAuditor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun auditInstalledApps(includeSystemApps: Boolean = false): List<AppPermissionReport> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        return packages
            .filter { includeSystemApps || (it.applicationInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) }
            .mapNotNull { pkg ->
                val perms = pkg.requestedPermissions ?: return@mapNotNull null
                if (perms.isEmpty()) return@mapNotNull null
                val findings = perms.map { perm ->
                    val (risk, explanation) = PermissionCatalog.explain(perm)
                    PermissionFinding(permission = perm, riskLevel = risk, explanation = explanation)
                }.sortedByDescending { it.riskLevel.ordinal }
                AppPermissionReport(
                    packageName = pkg.packageName,
                    appLabel = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName,
                    findings = findings
                )
            }
            .sortedByDescending { report -> report.findings.maxOfOrNull { it.riskLevel.ordinal } ?: -1 }
    }
}
