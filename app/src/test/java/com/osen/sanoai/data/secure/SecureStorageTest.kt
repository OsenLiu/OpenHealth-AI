package com.osen.sanoai.data.secure

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SecureStorageTest {

    private lateinit var secureStorage: SecureStorage

    @Before
    fun setup() {
        secureStorage = SecureStorage(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `save and get api key`() {
        val key = "test_key_123"
        secureStorage.saveApiKey(SecureStorage.KEY_GEMINI, key)
        
        val retrievedKey = secureStorage.getApiKey(SecureStorage.KEY_GEMINI)
        assertEquals(key, retrievedKey)
    }

    @Test
    fun `get non-existent api key returns null`() {
        val retrievedKey = secureStorage.getApiKey("non_existent")
        assertNull(retrievedKey)
    }

    @Test
    fun `overwrite existing api key`() {
        val key1 = "key1"
        val key2 = "key2"
        
        secureStorage.saveApiKey(SecureStorage.KEY_OPENAI, key1)
        secureStorage.saveApiKey(SecureStorage.KEY_OPENAI, key2)
        
        val retrievedKey = secureStorage.getApiKey(SecureStorage.KEY_OPENAI)
        assertEquals(key2, retrievedKey)
    }
}
