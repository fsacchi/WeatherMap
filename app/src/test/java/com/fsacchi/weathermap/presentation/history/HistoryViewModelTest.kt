package com.fsacchi.weathermap.presentation.history

import app.cash.turbine.test
import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.repository.HistoryRepository
import com.fsacchi.weathermap.domain.usecase.GetWeatherHistoryUseCase
import com.fsacchi.weathermap.util.TestFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class HistoryViewModelTest {

    @MockK
    lateinit var getHistory: GetWeatherHistoryUseCase

    @MockK
    lateinit var historyRepository: HistoryRepository

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HistoryViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = HistoryViewModel(getHistory, historyRepository)
    }

    @Test
    fun `init - loads history successfully, isEmpty is false`() = runTest {
        val items = listOf(TestFixtures.weatherHistory(), TestFixtures.weatherHistory(id = 2L))
        coEvery { getHistory() } returns Result.Success(items)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.items.size)
        assertFalse(state.isEmpty)
        assertNull(state.error)
    }

    @Test
    fun `init - empty history, isEmpty is true`() = runTest {
        coEvery { getHistory() } returns Result.Success(emptyList())

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.items.isEmpty())
        assertTrue(state.isEmpty)
    }

    @Test
    fun `init - error, sets error message`() = runTest {
        coEvery { getHistory() } returns Result.Error("DB error")

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("DB error", state.error)
    }

    @Test
    fun `isEmpty is false while loading`() = runTest {
        coEvery { getHistory() } returns Result.Success(emptyList())

        createViewModel()

        viewModel.uiState.test {
            val loading = awaitItem()
            if (loading.isLoading) {
                assertFalse(loading.isEmpty)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadHistory action reloads data`() = runTest {
        val initial = listOf(TestFixtures.weatherHistory())
        val updated = listOf(TestFixtures.weatherHistory(), TestFixtures.weatherHistory(id = 2L))
        coEvery { getHistory() } returnsMany listOf(Result.Success(initial), Result.Success(updated))

        createViewModel()
        advanceUntilIdle()

        viewModel.onAction(HistoryAction.LoadHistory)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.items.size)
    }

    @Test
    fun `DeleteItem success - calls deleteById then reloads list`() = runTest {
        val items = listOf(TestFixtures.weatherHistory(id = 1L), TestFixtures.weatherHistory(id = 2L))
        val afterDelete = listOf(TestFixtures.weatherHistory(id = 2L))
        coEvery { getHistory() } returnsMany listOf(Result.Success(items), Result.Success(afterDelete))
        coEvery { historyRepository.deleteById(1L) } returns Result.Success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.onAction(HistoryAction.DeleteItem(1L))
        advanceUntilIdle()

        coVerify(exactly = 1) { historyRepository.deleteById(1L) }
        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals(2L, viewModel.uiState.value.items.first().id)
    }

    @Test
    fun `DeleteItem error - does not reload list`() = runTest {
        val items = listOf(TestFixtures.weatherHistory(id = 1L))
        coEvery { getHistory() } returns Result.Success(items)
        coEvery { historyRepository.deleteById(1L) } returns Result.Error("Delete failed")

        createViewModel()
        advanceUntilIdle()

        viewModel.onAction(HistoryAction.DeleteItem(1L))
        advanceUntilIdle()

        coVerify(exactly = 1) { getHistory() }
    }

    @Test
    fun `DeleteAll success - reloads list, isEmpty is true`() = runTest {
        val items = listOf(TestFixtures.weatherHistory())
        coEvery { getHistory() } returnsMany listOf(Result.Success(items), Result.Success(emptyList()))
        coEvery { historyRepository.clearAll() } returns Result.Success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.onAction(HistoryAction.DeleteAll)
        advanceUntilIdle()

        coVerify(exactly = 1) { historyRepository.clearAll() }
        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `DismissError clears error`() = runTest {
        coEvery { getHistory() } returns Result.Error("DB error")

        createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.onAction(HistoryAction.DismissError)

        assertNull(viewModel.uiState.value.error)
    }
}
