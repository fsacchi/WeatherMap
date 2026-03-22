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
class GetWeatherByCityUseCaseTest {

    @MockK
    lateinit var repository: WeatherRepository

    private lateinit var useCase: GetWeatherByCityUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetWeatherByCityUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct city name`() = runTest {
        val expected = Result.Success(TestFixtures.weatherData(cityName = "Rio de Janeiro"))
        coEvery { repository.getWeatherByCity("Rio de Janeiro") } returns expected

        val result = useCase("Rio de Janeiro")

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getWeatherByCity("Rio de Janeiro") }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getWeatherByCity(any()) } returns Result.Error("City not found")

        val result = useCase("Unknown City")

        assertInstanceOf(Result.Error::class.java, result)
        assertEquals("City not found", (result as Result.Error).message)
    }

    @Test
    fun `invoke passes city name to repository unchanged`() = runTest {
        val cityName = "Belo Horizonte"
        coEvery { repository.getWeatherByCity(cityName) } returns Result.Success(TestFixtures.weatherData())

        useCase(cityName)

        coVerify { repository.getWeatherByCity(cityName) }
    }
}
