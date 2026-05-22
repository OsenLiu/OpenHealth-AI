package com.osen.sanoai

import android.app.Application
import androidx.room.Room
import com.osen.sanoai.data.api.AiRepository
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.local.AppDatabase
import com.osen.sanoai.data.repository.HealthRepository
import com.osen.sanoai.data.secure.SecureStorage
import com.osen.sanoai.di.NetworkModule

class SanoApplication : Application() {

    lateinit var database: AppDatabase
    lateinit var repository: HealthRepository
    lateinit var googleDriveService: GoogleDriveService

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        val secureStorage = SecureStorage(this)
        val aiRepository = AiRepository(
            openAiApi = NetworkModule.openAiApi,
            secureStorage = secureStorage,
            moshi = NetworkModule.moshi
        )

        googleDriveService = GoogleDriveService(this)

        repository = HealthRepository(
            healthDao = database.healthDao(),
            secureStorage = secureStorage,
            aiRepository = aiRepository
        )
    }
}
