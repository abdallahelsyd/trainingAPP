package com.example.trainingapp.data.repository

import com.example.trainingapp.data.local.MockLocalCache
import com.example.trainingapp.data.remote.MockLibraryApi
import com.example.trainingapp.domain.models.AudioInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val api: MockLibraryApi,
    private val cache: MockLocalCache
){
    fun getLibraryStream() = cache.cachedBooksStream

    suspend fun refreshLibrary() {
        try {
            val books = api.fetchAudioBooks()
            cache.saveBooks(books)
        }catch (e: Exception){
            // Handle exceptions (e.g., log them, rethrow, or ignore)
            e.printStackTrace()
        }
    }
    suspend fun loadInitialDataIfNeeded() {
        if (cache.isEmpty()) {
            refreshLibrary()
        }
    }

    suspend fun getAudioInfo(bookId: String): AudioInfo {
        return api.fetchAudioInfo(bookId)
    }

}