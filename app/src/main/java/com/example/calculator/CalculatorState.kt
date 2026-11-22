package com.example.calculator

data class HistoryEntry(
    val id: Long,
    val expression: String,
    val result: String,
)

sealed interface StatusKind {
    data object Ready : StatusKind
    data object Calculating : StatusKind
    data object Success : StatusKind
    data class ScientificSuccess(val operationLabel: String) : StatusKind
    data class Error(val errorType: CalculatorErrorType) : StatusKind
}

data class CalculatorUiState(
    val expression: String = "",
    val displayText: String = "0",
    val livePreview: String? = null,
    val statusKind: StatusKind = StatusKind.Ready,
    val justCalculated: Boolean = false,
    val isDarkTheme: Boolean = true,
    val history: List<HistoryEntry> = emptyList(),
) {
    val isError: Boolean get() = statusKind is StatusKind.Error
}
