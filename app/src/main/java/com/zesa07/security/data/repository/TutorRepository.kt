package com.zesa07.security.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zesa07.security.data.db.dao.TutorMessageDao
import com.zesa07.security.data.db.entities.TutorMessageEntity
import com.zesa07.security.domain.tutor.ClaudeTutorClient
import com.zesa07.security.domain.tutor.TutorTurn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the user's own Anthropic API key using EncryptedSharedPreferences (AES-256-GCM,
 * Android Keystore-backed master key) - never plaintext, never bundled with the app, never sent
 * anywhere except as the auth header of the tutor HTTPS call the user initiates.
 */
@Singleton
class TutorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tutorClient: ClaudeTutorClient,
    private val messageDao: TutorMessageDao
) {
    private val masterKey by lazy {
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    private val securePrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "zesa07_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveApiKey(apiKey: String) {
        securePrefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun getApiKey(): String? = securePrefs.getString(KEY_API_KEY, null)

    fun clearApiKey() {
        securePrefs.edit().remove(KEY_API_KEY).apply()
    }

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    fun observeMessages(): Flow<List<TutorMessageEntity>> = messageDao.observeAll()

    suspend fun sendMessage(userText: String, history: List<TutorTurn>): Result<String> {
        val key = getApiKey() ?: return Result.failure(IllegalStateException("No API key configured. Add your Anthropic API key in Settings."))
        messageDao.insert(TutorMessageEntity(timestampMillis = System.currentTimeMillis(), role = "user", content = userText))
        val result = tutorClient.sendMessage(key, history + TutorTurn("user", userText))
        result.onSuccess { reply ->
            messageDao.insert(TutorMessageEntity(timestampMillis = System.currentTimeMillis(), role = "assistant", content = reply))
        }
        return result
    }

    suspend fun clearHistory() = messageDao.clearAll()

    companion object {
        private const val KEY_API_KEY = "anthropic_api_key"
    }
}
