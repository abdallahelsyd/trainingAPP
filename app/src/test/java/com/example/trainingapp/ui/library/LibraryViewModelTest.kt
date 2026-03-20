package com.example.trainingapp.ui.library

import app.cash.turbine.test
import com.example.trainingapp.data.repository.LibraryRepository
import com.example.trainingapp.domain.models.AudioBook
import com.example.trainingapp.domain.models.LibraryUiState
import com.example.trainingapp.services.PlaybackService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRepository: LibraryRepository
    private lateinit var mockPlaybackService: PlaybackService
    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        mockPlaybackService = mockk()
    }

    @Test
    fun `when playback changes, ui state updates playing status without reloading`() = runTest {
        // ARRANGE: Set up the mock streams
        val fakeBook = AudioBook(id = "1", title = "Atomic Habits", author = "James Clear")

        // We use MutableStateFlows here so we can manually emit new values during the test
        val mockLibraryStream = MutableStateFlow(listOf(fakeBook))
        val mockPlaybackStream = MutableStateFlow<String?>(null)

        every { mockRepository.getLibraryStream() } returns mockLibraryStream
        every { mockPlaybackService.currentPlayingBookId } returns mockPlaybackStream

        // Mock the suspend functions
        coEvery { mockRepository.refreshLibrary() } returns Unit
        // Mock the async info fetcher to just return empty or delay (simulate success/fail)
        coEvery { mockRepository.getAudioInfo(any()) } throws Exception("Ignore secondary API for this test")

        // ACT: Initialize the ViewModel
        val viewModel = LibraryViewModel(mockRepository, mockPlaybackService)

        // ASSERT: Use Turbine to test the StateFlow sequence
        viewModel.uiState.test {
            // 1. The initial state should be Loading (from the initialValue of stateIn)
            assertTrue(awaitItem() is LibraryUiState.Loading)

            // Advance the virtual time so the init { loadData() } coroutine finishes
            advanceUntilIdle()

            // 2. After loading, we should get the Success state.
            // Since mockPlaybackStream is null, isPlaying should be FALSE.
            val firstSuccessState = awaitItem() as LibraryUiState.Success
            assertEquals(1, firstSuccessState.items.size)
            assertFalse(firstSuccessState.items.first().isPlaying)

            // 3. SIMULATE BACKGROUND EVENT: The player changes the track to Book "1"
            mockPlaybackStream.value = "1"

            // 4. VERIFY REACTIVITY: The combine block should instantly emit a new state
            val updatedSuccessState = awaitItem() as LibraryUiState.Success
            assertTrue(updatedSuccessState.items.first().isPlaying)

            // Tell Turbine we are done listening to the stream
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }
}