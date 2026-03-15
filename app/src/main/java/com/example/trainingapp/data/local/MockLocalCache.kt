package com.example.trainingapp.data.local


import com.example.trainingapp.domain.models.AudioBook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Ensure only one instance of the cache exists
class MockLocalCache @Inject constructor() {

    // The internal state representing our "database table"
    private val _cachedBooks = MutableStateFlow<List<AudioBook>>(emptyList())

    // The public stream that the Repository will observe
    val cachedBooksStream: Flow<List<AudioBook>> = _cachedBooks.asStateFlow()

    // Simulate an INSERT or UPDATE query
    suspend fun saveBooks(books: List<AudioBook>) {
        _cachedBooks.value = books
    }

    // Helper to check if cache is empty
    fun isEmpty(): Boolean = _cachedBooks.value.isEmpty()
}