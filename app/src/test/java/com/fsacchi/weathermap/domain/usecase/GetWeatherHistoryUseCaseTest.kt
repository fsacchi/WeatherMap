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
class GetWeatherHistoryUseCaseTest {

    @MockK
    lateinit var repository: HistoryRepository

    private lateinit var useCase: GetWeatherHistoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetWeatherHistoryUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository and returns history list`() = runTest {
        val history = listOf(TestFixtures.weatherHistory(), TestFixtures.weatherHistory(id = 2L, cityName = "Campinas"))
        coEvery { repository.getHistory() } returns Result.Success(history)

        val result = useCase()

        assertEquals(Result.Success(history), result)
        coVerify(exactly = 1) { repository.getHistory() }
    }

    @Test
    fun `invoke returns empty list when history is empty`() = runTest {
        coEvery { repository.getHistory() } returns Result.Success(emptyList())

        val result = useCase()

        assertEquals(Result.Success(emptyList<Nothing>()), result)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getHistory() } returns Result.Error("DB unavailable")

        val result = useCase()

        assertInstanceOf(Result.Error::class.java, result)
        assertEquals("DB unavailable", (result as Result.Error).message)
    }
}
