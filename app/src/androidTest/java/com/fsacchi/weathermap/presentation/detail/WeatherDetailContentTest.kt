package com.fsacchi.weathermap.presentation.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fsacchi.weathermap.ui.theme.WeatherMapTheme
import com.fsacchi.weathermap.util.TestInstrumentedFixtures
import com.fsacchi.weathermap.util.TestInstrumentedFixtures.defaultForecast
import com.fsacchi.weathermap.util.TestInstrumentedFixtures.defaultWeather
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun renderContent(uiState: WeatherDetailUiState, onAction: (WeatherDetailAction) -> Unit = {}) {
        composeTestRule.setContent {
            WeatherMapTheme {
                WeatherDetailContent(uiState = uiState, onAction = onAction)
            }
        }
    }

    @Test
    fun loadingState_showsProgressIndicator() {
        renderContent(WeatherDetailUiState(isLoading = true))

        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun loadedState_hidesProgressIndicator() {
        renderContent(WeatherDetailUiState(isLoading = false, weather = TestInstrumentedFixtures.defaultWeather()))

        composeTestRule.onNodeWithTag("loading_indicator").assertIsNotDisplayed()
    }

    @Test
    fun weatherLoaded_showsCityAndCountry() {
        renderContent(WeatherDetailUiState(weather = defaultWeather()))

        composeTestRule.onNodeWithText("São Paulo, BR").assertIsDisplayed()
    }

    @Test
    fun weatherLoaded_showsTemperature() {
        renderContent(WeatherDetailUiState(weather = defaultWeather()))

        composeTestRule.onNodeWithText("25°C").assertIsDisplayed()
    }

    @Test
    fun weatherLoaded_showsDescriptionCapitalized() {
        renderContent(WeatherDetailUiState(weather = defaultWeather()))

        composeTestRule.onNodeWithText("Clear sky").assertIsDisplayed()
    }

    @Test
    fun weatherLoaded_showsHumidityInDetailsCard() {
        renderContent(WeatherDetailUiState(weather = defaultWeather()))

        composeTestRule.onNodeWithText("70%").assertIsDisplayed()
    }

    @Test
    fun weatherLoaded_showsWindSpeedInDetailsCard() {
        renderContent(WeatherDetailUiState(weather = defaultWeather()))

        composeTestRule.onNodeWithText("5.0 m/s").assertIsDisplayed()
    }

    @Test
    fun weatherLoaded_showsPressureInDetailsCard() {
        renderContent(WeatherDetailUiState(weather = defaultWeather()))

        composeTestRule.onNodeWithText("1013 hPa").assertIsDisplayed()
    }

    @Test
    fun forecastLoaded_showsForecastCardTitle() {
        renderContent(WeatherDetailUiState(weather = defaultWeather(), forecast = defaultForecast()))

        composeTestRule.onNodeWithText("Previsão").assertIsDisplayed()
    }

    @Test
    fun emptyForecast_forecastCardTitleIsNotShown() {
        renderContent(WeatherDetailUiState(weather = defaultWeather(), forecast = emptyList()))

        composeTestRule.onNodeWithText("Previsão").assertIsNotDisplayed()
    }

    @Test
    fun errorState_showsErrorMessageInSnackbar() {
        renderContent(WeatherDetailUiState(error = "Algo deu errado"))

        composeTestRule.onNodeWithText("Algo deu errado").assertIsDisplayed()
    }

    @Test
    fun backButton_click_triggersNavigateBack() {
        var actionReceived: WeatherDetailAction? = null

        renderContent(
            uiState = WeatherDetailUiState(weather = defaultWeather()),
            onAction = { actionReceived = it }
        )

        composeTestRule.onNodeWithContentDescription("Voltar").performClick()

        assertTrue(actionReceived is WeatherDetailAction.NavigateBack)
    }

    @Test
    fun shareButton_click_triggersShare() {
        var actionReceived: WeatherDetailAction? = null

        renderContent(
            uiState = WeatherDetailUiState(weather = defaultWeather()),
            onAction = { actionReceived = it }
        )

        composeTestRule.onNodeWithContentDescription("Compartilhar").performClick()

        assertTrue(actionReceived is WeatherDetailAction.Share)
    }

    @Test
    fun dismissError_click_triggersDismissAction() {
        var actionReceived: WeatherDetailAction? = null

        renderContent(
            uiState = WeatherDetailUiState(error = "Erro de teste"),
            onAction = { actionReceived = it }
        )

        composeTestRule.onNodeWithText("OK").performClick()

        assertTrue(actionReceived is WeatherDetailAction.DismissError)
    }
}
