package com.example.trainingapp.data.remote

import com.example.trainingapp.domain.models.AudioBook
import com.example.trainingapp.domain.models.AudioInfo
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockLibraryApi @Inject constructor() {

    // Simulate a network call
    suspend fun fetchAudioBooks(): List<AudioBook> {
        delay(1500) // Simulate 1.5 second network latency

        return listOf(
            AudioBook("1", "Atomic Habits", "James Clear"),
            AudioBook("2", "Deep Work", "Cal Newport"),
            AudioBook("3", "The Pragmatic Programmer", "David Thomas"),
            AudioBook("4", "Clean Code", "Robert C. Martin"),
            AudioBook("5", "Designing Data-Intensive Applications", "Martin Kleppmann")
        )
    }

    suspend fun fetchAudioInfo(bookId: String): AudioInfo {
        delay(1000) // 1 second delay to prove the list renders before the data arrives
        return AudioInfo(
            likes = (100..5000).random(),
            durationString = "${(2..15).random()}h ${(10..59).random()}m"
        )
    }
}