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
class GetForecastUseCaseTest {

    @MockK
    lateinit var repository: WeatherRepository

    private lateinit var useCase: GetForecastUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetForecastUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct coordinates`() = runTest {
        val expected = Result.Success(TestFixtures.forecastList())
        coEvery { repository.getForecast(1.0, 2.0) } returns expected

        val result = useCase(1.0, 2.0)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getForecast(1.0, 2.0) }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getForecast(any(), any()) } returns Result.Error("Timeout")

        val result = useCase(0.0, 0.0)

        assertInstanceOf(Result.Error::class.java, result)
        assertEquals("Timeout", (result as Result.Error).message)
    }

    @Test
    fun `invoke returns empty list when forecast is empty`() = runTest {
        coEvery { repository.getForecast(any(), any()) } returns Result.Success(emptyList())

        val result = useCase(0.0, 0.0)

        assertEquals(Result.Success(emptyList<Nothing>()), result)
    }
}
