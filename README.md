# ZeSa07

**ZeSa07** is an Android cybersecurity education app that teaches ethical hacking and defensive
security concepts through completely isolated, legal training labs. Every "offensive" feature in
this app operates only against simulated targets bundled with the app or against devices the user
explicitly owns and confirms as their own authorized local-network lab.

> ZeSa07 is a training tool. It contains **no malware, no exploit payloads, no credential
> harvesters, and no code that attacks real third-party systems.** See
> [Safety & Privacy Model](#safety--privacy-model) below for exactly how this is enforced in code.

---

## Features

| # | Feature | Where |
|---|---|---|
| 1 | Dashboard | `ui/screens/dashboard` |
| 2 | Wi-Fi security education & nearby-network scanner | `ui/screens/wifiscanner`, `domain/wifi` |
| 3 | Local-network device discovery (authorized lab) | `ui/screens/networkdiscovery`, `domain/network/LocalNetworkScanner` |
| 4 | Port/service identification (authorized lab) | `ui/screens/portscanner`, `domain/network/LabPortScanner` |
| 5 | Password-strength tester (local test data only) | `ui/screens/passwordtester`, `domain/password` |
| 6 | Hashing & cryptography toolkit | `ui/screens/hashing`, `domain/crypto` |
| 7 | Android permission/security analyzer | `ui/screens/permissionanalyzer`, `domain/network/InstalledAppPermissionAuditor` |
| 8 | Safe malware-behavior simulator (no harmful actions) | `ui/screens/malwaresim`, `domain/simulator` |
| 9 | CTF challenge system | `ui/screens/ctf`, `domain/ctf/CtfChallengeCatalog` |
| 10 | Vulnerable virtual-lab exercises | `ui/screens/labs`, `domain/ctf/LabExerciseCatalog` |
| 11 | Security logs & scan history | `ui/screens/logs`, `data/repository/ScanHistoryRepository` |
| 12 | Claude-powered cybersecurity tutor | `ui/screens/tutor`, `domain/tutor/ClaudeTutorClient` |
| 13 | Learning progress & achievements | `ui/screens/progress`, `data/repository/ProgressRepository` |
| 14 | Dark hacker-style UI with animations | `ui/theme`, `ui/components` |

---

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3) for 100% declarative UI
- **Navigation Compose** for in-app navigation (bottom bar + detail routes)
- **Hilt** for dependency injection
- **Room** for the local database (scan logs, CTF/lab progress, achievements, tutor chat history)
- **EncryptedSharedPreferences** (Android Keystore-backed) for the user's own Claude API key
- **OkHttp** + **kotlinx.serialization** for the two categories of network calls this app makes:
  authorized-lab TCP probes and the optional Claude tutor HTTPS calls
- **Accompanist Permissions** for runtime permission flows
- **JUnit + Truth + kotlinx-coroutines-test** for unit tests

---

## Project structure

```
ZeSa07/
├── app/
│   ├── src/main/java/com/zesa07/security/
│   │   ├── ZeSa07Application.kt        # Hilt entry point + safety contract doc
│   │   ├── MainActivity.kt
│   │   ├── di/                         # Hilt modules
│   │   ├── data/
│   │   │   ├── db/                     # Room entities + DAOs + AppDatabase
│   │   │   └── repository/             # Repositories bridging DB + domain logic
│   │   ├── domain/
│   │   │   ├── model/                  # Shared data classes, permission risk catalog
│   │   │   ├── crypto/                 # HashingEngine (MD5/SHA1/SHA256/SHA512, AES-GCM demo)
│   │   │   ├── password/               # PasswordStrengthEngine
│   │   │   ├── network/                # LocalNetworkScanner, LabPortScanner, permission auditor
│   │   │   ├── wifi/                   # WifiInfoProvider (read-only beacon metadata)
│   │   │   ├── simulator/              # MalwareBehaviorSimulator (scripted narration only)
│   │   │   ├── ctf/                    # CtfChallengeCatalog, LabExerciseCatalog
│   │   │   └── tutor/                  # ClaudeTutorClient (Anthropic Messages API)
│   │   ├── ui/
│   │   │   ├── theme/                  # Dark hacker-style Compose theme
│   │   │   ├── navigation/             # NavGraph + Destination routes
│   │   │   ├── components/             # GlowCard, ConfirmScanDialog, RiskChip, etc.
│   │   │   └── screens/<feature>/      # One package per feature: Screen.kt + ViewModel.kt
│   │   └── util/
│   │       └── IpUtils.kt              # THE central safety gate (private-IP-only validation)
│   ├── src/test/java/com/zesa07/security/   # Unit tests
│   └── src/main/res/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md   (this file)
```

---

## Setup instructions

### Prerequisites

- **Android Studio** Koala (2024.1.1) or newer
- **JDK 17**
- **Android SDK** platform 34, build-tools 34.x
- A physical device or emulator running **API 26+** (Android 8.0+)

### 1. Open the project

```bash
git clone <this-repo>
cd ZeSa07
```

Open the `ZeSa07/` folder in Android Studio and let Gradle sync. All dependencies resolve from
`google()` and `mavenCentral()` — no private repositories are required.

### 2. Build & run

```bash
./gradlew assembleDebug
./gradlew installDebug   # with a device/emulator connected
```

or just click **Run** in Android Studio.

### 3. (Optional) Enable the AI Tutor

The Claude-powered tutor (feature #12) requires your own Anthropic API key:

1. Get a key at [console.anthropic.com](https://console.anthropic.com).
2. In the app, go to **Settings → Anthropic API Key**, paste your key, and tap **Save Key**.
3. The key is stored on-device using `EncryptedSharedPreferences` (AES-256, Android
   Keystore-backed master key) and is **only** ever sent as the `x-api-key` header of your own
   tutor chat requests to `https://api.anthropic.com`. It is never bundled with the app, logged,
   or transmitted anywhere else.

All other 13 features work fully offline with zero configuration.

### 4. Run tests

```bash
./gradlew testDebugUnitTest
```

Unit tests cover:
- `IpUtilsTest` — the private-IP-only safety gate (public IPs, malformed input, edge cases)
- `NetworkScannerSafetyTest` — confirms the scanner/port-scanner classes throw and refuse to run
  against any non-private target
- `HashingEngineTest` — known-answer tests (KATs) for MD5/SHA-256, AES-GCM round trip, salting
- `PasswordStrengthEngineTest` — entropy scoring, common-password/keyboard-pattern detection
- `CtfChallengeCatalogTest` — flag-hash integrity (flags are never stored in plaintext)

---

## Safety & Privacy Model

This section documents, concretely, how each of the safety requirements is enforced in code —
not just as a policy statement, but as something you can point to and verify.

### 1. "Never attack real devices, accounts, networks, or people"

Every feature that touches a network target routes through **`util/IpUtils.kt`**, which validates
that an address is RFC1918 private (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`), loopback
(`127.0.0.0/8`/`localhost`), or link-local (`169.254.0.0/16`) before anything else happens:

- `domain/network/LocalNetworkScanner.discoverDevices()` / `probeSingle()` — `require()`s
  `IpUtils.isAuthorizedLabSubnetBase()` / `isAuthorizedLabAddress()`, **throwing** if the check
  fails.
- `domain/network/LabPortScanner.scanPorts()` — same pattern.
- Both are unit-tested in `NetworkScannerSafetyTest` to confirm public IPs are always refused.

There is no code path (UI, ViewModel, or domain layer) that constructs a scan target from
anything other than user-typed text that then passes through this gate.

### 2. "Never retrieve, crack, steal, or reveal real passwords"

- `PasswordTesterViewModel` never persists the raw password string to Room, DataStore, or any
  log — only the *derived analysis* (verdict/entropy/issues) would ever be eligible for logging,
  and even that isn't currently logged.
- The Hashing Toolkit only hashes text the user types into a local field for demonstration; it
  never reads any stored credential, keychain, browser password store, or account data.
- The CTF system's flags are stored as **SHA-256 hashes only** (`CtfChallenge.flagHashSha256`);
  the plaintext flag never exists in the codebase or the database — verified by
  `CtfChallengeCatalogTest`.

### 3. "Never provide malware, ransomware, spyware, credential stealers, RATs, persistence
mechanisms, exploit payloads, or camera/location surveillance"

- `domain/simulator/MalwareBehaviorSimulator.kt` is a **hard-coded list of `SimBehaviorStep`
  strings** — titles, MITRE ATT&CK IDs, and narration text. There is no executable payload logic
  of any kind anywhere in this class or the screen that renders it; it performs no file, network,
  permission, or process actions. This is stated explicitly in the file's doc comment and is true
  of the entire codebase, not just that one class.
- The vulnerable-lab exercises (`domain/ctf/LabExerciseCatalog.kt`) are conceptual walkthroughs
  with an in-app **simulated** login-bypass demo (`LabDetailViewModel.runSqliSimulation()`) that
  does simple string pattern-matching — there is no real database, no real query execution, and
  no reusable injection tooling.
- Camera and microphone permissions are declared in the manifest for completeness/extensibility
  but no current screen requests or uses them for surveillance; any future use must follow the
  same "runtime permission + visible active indicator" pattern already used for location (see
  `ActiveIndicatorDot` in `ui/components`).

### 4. "All offensive demonstrations must operate only against simulated targets bundled inside
the app or explicitly user-created local lab targets"

Enforced by the `IpUtils` gate described in (1). The bundled CTF challenges and lab exercises
never point at, and never require, any real external host.

### 5. "Require explicit confirmation before any network scan"

Every scan-triggering screen (Wi-Fi scanner, Network Discovery, Port Scanner) shows
**`ui/components/ConfirmScanDialog.kt`** immediately before the scan fires, and the "Proceed"
button explicitly states: *"By continuing you confirm this target is a device YOU own or are
explicitly authorized to test."* Scans never fire directly from a button's `onClick`; they always
route `requestScanConfirmation() → dialog → confirmAndScan()`.

### 6. "Camera, microphone, Bluetooth, and location features must require Android runtime
permissions and clearly show when they are active"

- Location is requested via Accompanist's `rememberMultiplePermissionsState` before the Wi-Fi
  scan runs (`WifiScannerScreen.kt`), and an `ActiveIndicatorDot` pulses while a scan is running
  so the user can see the sensor is in use.
- Camera/microphone/Bluetooth permissions are declared but unused by default; the same
  request-then-indicate pattern is the required approach for any future feature that uses them.

### What the app sends off-device (and what it never does)

The **only** feature that makes an external network call with user data is the **AI Tutor**
(`domain/tutor/ClaudeTutorClient.kt`), and it only ever sends the chat text the user typed plus a
fixed system prompt instructing the model to refuse real-attack assistance. It never sends scan
results, device identifiers, location, or installed-app data. The `network_security_config.xml`
additionally blocks cleartext HTTP to any host that isn't a private-lab address, so even a bug
elsewhere in the app cannot leak data over an unencrypted channel to a public host.

---

## Legal notice

ZeSa07 is provided for **legal, authorized security education only**. Unauthorized access to
computer systems, networks, or accounts you do not own or have explicit written permission to
test is illegal in most jurisdictions (e.g., the U.S. Computer Fraud and Abuse Act, UK Computer
Misuse Act, and equivalent laws elsewhere). Use this app only against the bundled simulations or
equipment you personally own and control.
