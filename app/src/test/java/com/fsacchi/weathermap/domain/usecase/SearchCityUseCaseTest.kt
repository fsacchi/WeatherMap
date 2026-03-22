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
class SearchCityUseCaseTest {

    @MockK
    lateinit var repository: WeatherRepository

    private lateinit var useCase: SearchCityUseCase

    @BeforeEach
    fun setUp() {
        useCase = SearchCityUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct query`() = runTest {
        val results = listOf(TestFixtures.geocodingResult(), TestFixtures.geocodingResult(name = "Santos"))
        coEvery { repository.searchCity("São") } returns Result.Success(results)

        val result = useCase("São")

        assertEquals(Result.Success(results), result)
        coVerify(exactly = 1) { repository.searchCity("São") }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.searchCity(any()) } returns Result.Error("API error")

        val result = useCase("query")

        assertInstanceOf(Result.Error::class.java, result)
        assertEquals("API error", (result as Result.Error).message)
    }

    @Test
    fun `invoke returns empty list when no results found`() = runTest {
        coEvery { repository.searchCity(any()) } returns Result.Success(emptyList())

        val result = useCase("xyz")

        assertEquals(Result.Success(emptyList<Nothing>()), result)
    }
}
