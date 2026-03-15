package com.example.trainingapp.services


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackService @Inject constructor() {

    private val _currentPlayingBookId = MutableStateFlow<String?>(null)
    val currentPlayingBookId: StateFlow<String?> = _currentPlayingBookId.asStateFlow()

    init {
        // Simulate a media player randomly changing tracks in the background
        CoroutineScope(Dispatchers.Default).launch {
            val mockIds = listOf("1", "3", "5", null, "2")
            var index = 0
            while (true) {
                delay(5000) // Change track every 5 seconds
                _currentPlayingBookId.value = mockIds[index % mockIds.size]
                index++
            }
        }
    }
}