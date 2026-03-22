package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.repository.HistoryRepository
import com.fsacchi.weathermap.util.TestFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SaveWeatherHistoryUseCaseTest {

    @MockK
    lateinit var repository: HistoryRepository

    private lateinit var useCase: SaveWeatherHistoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = SaveWeatherHistoryUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct WeatherData`() = runTest {
        val weather = TestFixtures.weatherData()
        coEvery { repository.saveWeather(weather) } returns Result.Success(Unit)

        val result = useCase(weather)

        assertEquals(Result.Success(Unit), result)
        coVerify(exactly = 1) { repository.saveWeather(weather) }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val weather = TestFixtures.weatherData()
        coEvery { repository.saveWeather(any()) } returns Result.Error("DB error")

        val result = useCase(weather)

        assertInstanceOf(Result.Error::class.java, result)
        assertEquals("DB error", (result as Result.Error).message)
    }
}
