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

    companion object {
        const val KEY_GEMINI = "gemini_api_key"
        const val KEY_OPENAI = "openai_api_key"
        const val KEY_BYTEPLUS = "byteplus_api_key"
    }
}
