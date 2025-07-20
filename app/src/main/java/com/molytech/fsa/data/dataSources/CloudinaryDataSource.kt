package com.molytech.fsa.data.dataSources

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

interface CloudinaryDataSource {
    suspend fun uploadImage(imageUri: Uri): String
}

class CloudinaryDataSourceImpl(
    private val context: Context
) : CloudinaryDataSource {

    private val cloudinary by lazy {
        Cloudinary(
            hashMapOf<String, Any>(
                "cloud_name" to "dqlmsi4oj",
                "api_key" to "727232429138216",
                "api_secret" to "mg_UjUKk5hvEdt6OyIh4v3CBN0w"
            )
        )
    }

    override suspend fun uploadImage(imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val file = uriToFile(imageUri)
                val uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                val imageUrl = uploadResult["secure_url"] as String

                // Limpiar archivo temporal
                file.delete()

                imageUrl
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        file.outputStream().use { inputStream.copyTo(it) }
        return file
    }
}
