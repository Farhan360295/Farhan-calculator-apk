package com.example

import android.content.Context
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Farhan Calc", appName)
    }

    @Test
    fun `calculator performs arithmetic and scientific operations`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("display_result").assertTextEquals("0")

        composeTestRule.onNodeWithTag("btn_7").performClick()
        composeTestRule.onNodeWithTag("btn_mul").performClick()
        composeTestRule.onNodeWithTag("btn_6").performClick()
        composeTestRule.onNodeWithTag("btn_eq").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("display_result").assertTextEquals("42")

        composeTestRule.onNodeWithTag("btn_ac").performClick()
        composeTestRule.onNodeWithTag("btn_3").performClick()
        composeTestRule.onNodeWithTag("btn_0").performClick()
        composeTestRule.onNodeWithTag("btn_sin").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("display_result").assertTextEquals("0.5")
    }
}
