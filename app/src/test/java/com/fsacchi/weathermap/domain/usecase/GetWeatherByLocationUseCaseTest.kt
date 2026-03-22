package com.fsacchi.weathermap.domain.usecase

import com.fsacchi.weathermap.core.util.Result
import com.fsacchi.weathermap.domain.repository.WeatherRepository
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
class GetWeatherByLocationUseCaseTest {

    @MockK
    lateinit var repository: WeatherRepository

    private lateinit var useCase: GetWeatherByLocationUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetWeatherByLocationUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct coordinates`() = runTest {
        val expected = Result.Success(TestFixtures.weatherData())
        coEvery { repository.getWeatherByLocation(1.0, 2.0) } returns expected

        val result = useCase(1.0, 2.0)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getWeatherByLocation(1.0, 2.0) }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getWeatherByLocation(any(), any()) } returns Result.Error("Network error")

        val result = useCase(0.0, 0.0)

        assertInstanceOf(Result.Error::class.java, result)
        assertEquals("Network error", (result as Result.Error).message)
    }

    @Test
    fun `invoke passes lat and lon to repository unchanged`() = runTest {
        val lat = -23.5505
        val lon = -46.6333
        coEvery { repository.getWeatherByLocation(lat, lon) } returns Result.Success(TestFixtures.weatherData())

        useCase(lat, lon)

        coVerify { repository.getWeatherByLocation(lat, lon) }
    }
}
