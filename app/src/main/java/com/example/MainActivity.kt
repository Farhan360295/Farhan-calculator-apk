package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.CalculatorViewModel
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.theme.FarhanCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            val viewModel: CalculatorViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isDarkTheme) {
                val barStyle = if (uiState.isDarkTheme) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    )
                }
                enableEdgeToEdge(
                    statusBarStyle = barStyle,
                    navigationBarStyle = barStyle,
                )
            }

            FarhanCalculatorTheme(darkTheme = uiState.isDarkTheme) {
                CalculatorScreen(
                    uiState = uiState,
                    onTokenClick = viewModel::appendToken,
                    onScientificClick = viewModel::applyScientific,
                    onClearAll = viewModel::clearAll,
                    onDeleteLast = viewModel::deleteLast,
                    onCalculate = viewModel::calculate,
                    onToggleTheme = viewModel::toggleTheme,
                    onSelectHistoryResult = viewModel::useHistoryResult,
                    onClearHistory = viewModel::clearHistory,
                )
            }
        }
    }
}
