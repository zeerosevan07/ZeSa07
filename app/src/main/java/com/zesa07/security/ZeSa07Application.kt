package com.zesa07.security

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * ZeSa07 - Cybersecurity education application.
 *
 * SAFETY CONTRACT (enforced throughout the codebase, see README#safety-and-privacy-model):
 *  1. No code path in this app ever attacks, scans without confirmation, or exfiltrates data
 *     from a real third-party device, account, or network.
 *  2. All "offensive" demonstrations (port scanning, device discovery, malware-behavior sim)
 *     operate ONLY against: (a) an in-app simulated target catalog, or (b) IP addresses the
 *     user explicitly enters AND that resolve to RFC1918 private / loopback ranges (see
 *     [com.zesa07.security.util.IpUtils]), i.e. equipment on the user's own local network.
 *  3. No malware, exploit payload, credential harvester, or persistence mechanism is ever
 *     generated or executed. The "malware simulator" only plays back a scripted, harmless,
 *     purely-visual timeline (see [com.zesa07.security.domain.simulator.MalwareBehaviorSimulator]).
 *  4. Every scan/discovery action requires an explicit user confirmation dialog immediately
 *     before it runs (see [com.zesa07.security.ui.components.ConfirmScanDialog]).
 */
@HiltAndroidApp
class ZeSa07Application : Application()
