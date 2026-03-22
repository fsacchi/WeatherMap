package com.fsacchi.weathermap.presentation.detail

import app.cash.turbine.test
import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.usecase.GetForecastUseCase
import com.fsacchi.weathermap.domain.usecase.GetWeatherByLocationUseCase
import com.fsacchi.weathermap.util.TestFixtures
import io.mockk.coEvery
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
class WeatherDetailViewModelTest {

    @MockK
    lateinit var getWeatherByLocation: GetWeatherByLocationUseCase

    @MockK
    lateinit var getForecast: GetForecastUseCase

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WeatherDetailViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherDetailViewModel(getWeatherByLocation, getForecast)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadWeather success - final state has weather, forecast, no error, not loading`() = runTest {
        coEvery { getWeatherByLocation(1.0, 2.0) } returns Result.Success(TestFixtures.weatherData())
        coEvery { getForecast(1.0, 2.0) } returns Result.Success(TestFixtures.forecastList())

        viewModel.uiState.test {
            awaitItem()

            viewModel.onAction(WeatherDetailAction.LoadWeather(1.0, 2.0))
            advanceUntilIdle()

            var latest = awaitItem()
            while (latest.isLoading || latest.weather == null || latest.forecast.isEmpty()) {
                latest = awaitItem()
            }

            assertNotNull(latest.weather)
            assertEquals(3, latest.forecast.size)
            assertNull(latest.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadWeather emits loading state before result`() = runTest {
        coEvery { getWeatherByLocation(any(), any()) } returns Result.Success(TestFixtures.weatherData())
        coEvery { getForecast(any(), any()) } returns Result.Success(TestFixtures.forecastList())

        viewModel.uiState.test {
            awaitItem() 

            viewModel.onAction(WeatherDetailAction.LoadWeather(1.0, 2.0))
            advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadWeather weather error - sets error message, not loading`() = runTest {
        coEvery { getWeatherByLocation(any(), any()) } returns Result.Error("Service unavailable")
        coEvery { getForecast(any(), any()) } returns Result.Success(TestFixtures.forecastList())

        viewModel.uiState.test {
            awaitItem() 

            viewModel.onAction(WeatherDetailAction.LoadWeather(1.0, 2.0))
            advanceUntilIdle()

            var latest = awaitItem()
            while (latest.isLoading) {
                latest = awaitItem()
            }

            assertEquals("Service unavailable", latest.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadWeather forecast error - does not set error, weather is present`() = runTest {
        coEvery { getWeatherByLocation(any(), any()) } returns Result.Success(TestFixtures.weatherData())
        coEvery { getForecast(any(), any()) } returns Result.Error("Forecast unavailable")

        viewModel.uiState.test {
            awaitItem() 

            viewModel.onAction(WeatherDetailAction.LoadWeather(1.0, 2.0))
            advanceUntilIdle()

            var latest = awaitItem()
            while (latest.isLoading || latest.weather == null) {
                latest = awaitItem()
            }

            assertNotNull(latest.weather)
            assertNull(latest.error)
            assertTrue(latest.forecast.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Share action sets shareTriggered to true`() = runTest {
        viewModel.uiState.test {
            awaitItem() 

            viewModel.onAction(WeatherDetailAction.Share)

            val state = awaitItem()
            assertTrue(state.shareTriggered)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ResetShare action sets shareTriggered to false`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            viewModel.onAction(WeatherDetailAction.Share)
            awaitItem()

            viewModel.onAction(WeatherDetailAction.ResetShare)

            val state = awaitItem()
            assertFalse(state.shareTriggered)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ShowError action sets error message`() = runTest {
        viewModel.uiState.test {
            awaitItem() 

            viewModel.onAction(WeatherDetailAction.ShowError("Something went wrong"))

            val state = awaitItem()
            assertEquals("Something went wrong", state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissError action clears error`() = runTest {
        viewModel.uiState.test {
            awaitItem() 

            viewModel.onAction(WeatherDetailAction.ShowError("Error"))
            awaitItem()

            viewModel.onAction(WeatherDetailAction.DismissError)

            val state = awaitItem()
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
