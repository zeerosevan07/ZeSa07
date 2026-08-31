package com.zesa07.security.domain.tutor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin client for the "Claude-powered cybersecurity tutor" feature.
 *
 * Privacy notes (see README#claude-tutor-privacy):
 *  - This is the ONLY feature in the app that sends any data off-device, and it only ever sends
 *    the tutor chat text the user typed plus a fixed system prompt - never scan results, device
 *    identifiers, location, or any other locally-collected data.
 *  - The API key is supplied by the user (BYO key) via Settings and stored using
 *    EncryptedSharedPreferences (Android Keystore-backed); it is never hard-coded or bundled in
 *    the app, and is only attached as the request header for this one HTTPS call.
 *  - All calls use HTTPS to api.anthropic.com only (see network_security_config.xml, which
 *    blocks cleartext to non-lab hosts).
 */
@Singleton
class ClaudeTutorClient @Inject constructor(
    private val httpClient: OkHttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val endpoint = "https://api.anthropic.com/v1/messages"

    private val systemPrompt = """
        You are the ZeSa07 in-app cybersecurity tutor. Teach ethical hacking and defensive
        security concepts for LEGAL, AUTHORIZED educational purposes only. Refuse to help attack,
        access, or compromise any real system, account, or network the learner does not own or
        control. Refuse requests for working malware, exploit payloads, or real credential
        material. Prefer conceptual explanations, defensive guidance, and pointers to the app's
        own simulated labs and CTF challenges.
    """.trimIndent()

    suspend fun sendMessage(
        apiKey: String,
        conversation: List<TutorTurn>,
        model: String = "claude-sonnet-4-5"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val payload = MessagesRequest(
                model = model,
                maxTokens = 1024,
                system = systemPrompt,
                messages = conversation.map { Msg(role = it.role, content = it.content) }
            )
            val body = json.encodeToString(MessagesRequest.serializer(), payload)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(endpoint)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(body)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Tutor API error ${response.code}: $text"))
                }
                val parsed = json.decodeFromString(MessagesResponse.serializer(), text)
                val reply = parsed.content.firstOrNull { it.type == "text" }?.text
                    ?: "(no response text returned)"
                Result.success(reply)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class TutorTurn(val role: String, val content: String)

@Serializable
private data class MessagesRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val system: String,
    val messages: List<Msg>
)

@Serializable
private data class Msg(val role: String, val content: String)

@Serializable
private data class MessagesResponse(val content: List<ContentBlock> = emptyList())

@Serializable
private data class ContentBlock(val type: String, val text: String? = null)
