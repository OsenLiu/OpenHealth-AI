package com.osen.sanoai.data.secure

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiKey(provider: String, apiKey: String) {
        sharedPreferences.edit().putString(provider, apiKey).apply()
    }

    fun getApiKey(provider: String): String? {
        return sharedPreferences.getString(provider, null)
    }

    fun saveSelectedProvider(provider: String) {
        sharedPreferences.edit().putString(KEY_SELECTED_PROVIDER, provider).apply()
    }

    fun getSelectedProvider(): String? {
        return sharedPreferences.getString(KEY_SELECTED_PROVIDER, null)
    }

    fun saveModelName(provider: String, modelName: String) {
        sharedPreferences.edit().putString("${provider}_model", modelName).apply()
    }

    fun getModelName(provider: String): String? {
        return sharedPreferences.getString("${provider}_model", null)
    }

    companion object {
        const val KEY_GEMINI = "gemini_api_key"
        const val KEY_OPENAI = "openai_api_key"
        const val KEY_BYTEPLUS = "byteplus_api_key"
        const val KEY_SELECTED_PROVIDER = "selected_ai_provider"
    }
}
