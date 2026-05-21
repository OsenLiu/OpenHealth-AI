package com.osen.sanoai.data.backup

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class GoogleDriveService(private val context: Context) {

    private val driveService: Drive? by lazy {
        // This requires the user to be signed in. 
        // In a real app, we'd pass the account name here.
        null 
    }

    suspend fun backupDatabase(accountName: String) = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccountName = accountName
            
            val service = Drive.Builder(NetHttpTransport(), GsonFactory(), credential)
                .setApplicationName("SanoAI")
                .build()

            val dbFile = context.getDatabasePath("sanoai_db")
            if (!dbFile.exists()) return@withContext false

            val fileMetadata = com.google.api.services.drive.model.File()
            fileMetadata.name = "sanoai_db_backup"
            fileMetadata.parents = listOf("appDataFolder")

            val mediaContent = FileContent("application/octet-stream", dbFile)
            
            // Check if file already exists
            val existingFiles = service.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = 'sanoai_db_backup'")
                .execute()
            
            if (existingFiles.files.isNotEmpty()) {
                service.files().update(existingFiles.files[0].id, fileMetadata, mediaContent).execute()
            } else {
                service.files().create(fileMetadata, mediaContent).execute()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun restoreDatabase(accountName: String) = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccountName = accountName
            
            val service = Drive.Builder(NetHttpTransport(), GsonFactory(), credential)
                .setApplicationName("SanoAI")
                .build()

            val existingFiles = service.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = 'sanoai_db_backup'")
                .execute()

            if (existingFiles.files.isEmpty()) return@withContext false

            val fileId = existingFiles.files[0].id
            val dbFile = context.getDatabasePath("sanoai_db")
            
            FileOutputStream(dbFile).use { outputStream ->
                service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
