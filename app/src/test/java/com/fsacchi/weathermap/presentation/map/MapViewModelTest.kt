package com.fsacchi.weathermap.presentation.map

import android.app.Application
import android.location.Location
import app.cash.turbine.test
import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.usecase.GetWeatherByLocationUseCase
import com.fsacchi.weathermap.domain.usecase.SaveWeatherHistoryUseCase
import com.fsacchi.weathermap.domain.usecase.SearchCityUseCase
import com.fsacchi.weathermap.util.TestFixtures
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class MapViewModelTest {

    @MockK
    lateinit var getWeatherByLocation: GetWeatherByLocationUseCase

    @MockK
    lateinit var searchCity: SearchCityUseCase

    @MockK
    lateinit var saveWeatherHistory: SaveWeatherHistoryUseCase

    private val application = mockk<Application>(relaxed = true)
    private val mockFusedLocationClient = mockk<FusedLocationProviderClient>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MapViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(LocationServices::class)
        every { LocationServices.getFusedLocationProviderClient(application) } returns mockFusedLocationClient
        viewModel = MapViewModel(application, getWeatherByLocation, searchCity, saveWeatherHistory)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(LocationServices::class)
    }

    @Nested
    @DisplayName("LoadWeatherByLocation")
    inner class LoadWeatherByLocationTests {

        @Test
        fun `success - updates pinWeatherData and coordinates`() = runTest {
            val weather = TestFixtures.weatherData(lat = 10.0, lon = 20.0)
            coEvery { getWeatherByLocation(10.0, 20.0) } returns Result.Success(weather)

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.LoadWeatherByLocation(10.0, 20.0))
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading || latest.pinWeatherData == null) {
                    latest = awaitItem()
                }

                assertFalse(latest.isLoading)
                assertNotNull(latest.pinWeatherData)
                assertEquals(10.0, latest.currentLat)
                assertEquals(20.0, latest.currentLon)
                assertNull(latest.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `error - sets error message`() = runTest {
            coEvery { getWeatherByLocation(any(), any()) } returns Result.Error("Connection refused")

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.LoadWeatherByLocation(1.0, 2.0))
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading) {
                    latest = awaitItem()
                }

                assertEquals("Connection refused", latest.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `TapOnMap behaves same as LoadWeatherByLocation`() = runTest {
            val weather = TestFixtures.weatherData(lat = 5.0, lon = 15.0)
            coEvery { getWeatherByLocation(5.0, 15.0) } returns Result.Success(weather)

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.TapOnMap(5.0, 15.0))
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading || latest.pinWeatherData == null) {
                    latest = awaitItem()
                }

                assertEquals(5.0, latest.currentLat)
                assertEquals(15.0, latest.currentLon)
                assertNotNull(latest.pinWeatherData)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("SearchCity")
    inner class SearchCityTests {

        @Test
        fun `query shorter than 3 chars - use case not called, results cleared`() = runTest {
            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.SearchCity("SP"))
                advanceUntilIdle()

                coVerify(exactly = 0) { searchCity(any()) }

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `rapid input - only last query reaches use case`() = runTest {
            coEvery { searchCity(any()) } returns Result.Success(emptyList())

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.SearchCity("São"))
                viewModel.onAction(MapAction.SearchCity("São P"))
                viewModel.onAction(MapAction.SearchCity("São Paulo"))

                advanceTimeBy(500)
                advanceUntilIdle()

                coVerify(exactly = 0) { searchCity("São") }
                coVerify(exactly = 0) { searchCity("São P") }
                coVerify(exactly = 1) { searchCity("São Paulo") }

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("SelectSearchResult")
    inner class SelectSearchResultTests {

        @Test
        fun `clears results, sets query, loads weather, saves history`() = runTest {
            val geocodingResult = TestFixtures.geocodingResult(name = "Campinas", lat = -22.9, lon = -47.0)
            val weather = TestFixtures.weatherData(lat = -22.9, lon = -47.0)
            coEvery { getWeatherByLocation(-22.9, -47.0) } returns Result.Success(weather)
            coEvery { saveWeatherHistory(weather) } returns Result.Success(Unit)

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.SelectSearchResult(geocodingResult))
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading || latest.pinWeatherData == null) {
                    latest = awaitItem()
                }

                assertTrue(latest.searchResults.isEmpty())
                assertEquals("Campinas", latest.searchQuery)
                assertNotNull(latest.pinWeatherData)
                coVerify(exactly = 1) { saveWeatherHistory(weather) }

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("RecenterOnGps")
    inner class RecenterOnGpsTests {

        @Test
        fun `valid location - loads weather for device coordinates`() = runTest {
            val mockLocation = mockk<Location>()
            every { mockLocation.latitude } returns 10.0
            every { mockLocation.longitude } returns 20.0

            val mockTask = mockk<Task<Location>>()
            val listenerSlot = slot<OnSuccessListener<Location>>()
            every { mockFusedLocationClient.lastLocation } returns mockTask
            every { mockTask.addOnSuccessListener(capture(listenerSlot)) } answers {
                listenerSlot.captured.onSuccess(mockLocation)
                mockTask
            }

            val weather = TestFixtures.weatherData(lat = 10.0, lon = 20.0)
            coEvery { getWeatherByLocation(10.0, 20.0) } returns Result.Success(weather)

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.RecenterOnGps)
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading || latest.pinWeatherData == null) {
                    latest = awaitItem()
                }

                assertEquals(10.0, latest.currentLat)
                assertEquals(20.0, latest.currentLon)
                assertNotNull(latest.pinWeatherData)

                cancelAndIgnoreRemainingEvents()
            }
        }

        @Test
        fun `null location - falls back to Sao Paulo coordinates`() = runTest {
            val mockTask = mockk<Task<Location>>()
            every { mockFusedLocationClient.lastLocation } returns mockTask
            every { mockTask.addOnSuccessListener(any()) } answers {
                @Suppress("UNCHECKED_CAST")
                val listener = firstArg<Any>() as OnSuccessListener<Location?>
                listener.onSuccess(null)
                mockTask
            }

            val weather = TestFixtures.weatherData(lat = -23.5, lon = -46.6)
            coEvery { getWeatherByLocation(-23.5, -46.6) } returns Result.Success(weather)

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.RecenterOnGps)
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading || latest.pinWeatherData == null) {
                    latest = awaitItem()
                }

                assertEquals(-23.5, latest.currentLat)
                assertEquals(-46.6, latest.currentLon)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Nested
    @DisplayName("DismissError")
    inner class DismissErrorTests {

        @Test
        fun `clears error`() = runTest {
            coEvery { getWeatherByLocation(any(), any()) } returns Result.Error("Network error")

            viewModel.uiState.test {
                awaitItem() 

                viewModel.onAction(MapAction.LoadWeatherByLocation(1.0, 2.0))
                advanceUntilIdle()

                var latest = awaitItem()
                while (latest.isLoading) {
                    latest = awaitItem()
                }
                assertNotNull(latest.error)

                viewModel.onAction(MapAction.DismissError)
                advanceUntilIdle()

                val cleared = awaitItem()
                assertNull(cleared.error)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
