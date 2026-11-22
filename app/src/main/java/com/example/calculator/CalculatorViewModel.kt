package com.example.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var nextHistoryId = 1L

    fun appendToken(token: String) {
        _uiState.update { state ->
            var baseExpr = if (state.isError) "" else state.expression
            var justCalculated = state.justCalculated

            if (justCalculated) {
                val startsFresh = token.length == 1 &&
                    (token[0].isDigit() || token == "." || token == "π" || token == "e" || token == "(")
                if (startsFresh) {
                    baseExpr = ""
                }
                justCalculated = false
            }

            // Prevent double decimal points in the same number segment
            if (token == ".") {
                val lastSegment = baseExpr.takeLastWhile { it.isDigit() || it == '.' }
                if (lastSegment.contains('.')) {
                    return@update state
                }
                val prefix = if (lastSegment.isEmpty()) "0." else "."
                val nextExpr = baseExpr + prefix
                return@update updateCalculatingState(state, nextExpr)
            }

            val nextExpr = baseExpr + token
            updateCalculatingState(state, nextExpr)
        }
    }

    fun clearAll() {
        _uiState.update { state ->
            state.copy(
                expression = "",
                displayText = "0",
                livePreview = null,
                statusKind = StatusKind.Ready,
                justCalculated = false,
            )
        }
    }

    fun deleteLast() {
        _uiState.update { state ->
            if (state.isError || state.expression.isEmpty()) {
                return@update state.copy(
                    expression = "",
                    displayText = "0",
                    livePreview = null,
                    statusKind = StatusKind.Ready,
                    justCalculated = false,
                )
            }
            val trimmed = state.expression.dropLast(1)
            if (trimmed.isEmpty()) {
                state.copy(
                    expression = "",
                    displayText = "0",
                    livePreview = null,
                    statusKind = StatusKind.Ready,
                    justCalculated = false,
                )
            } else {
                updateCalculatingState(state.copy(justCalculated = false), trimmed)
            }
        }
    }

    fun calculate() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isBlank()) {
                return@update state.copy(
                    statusKind = StatusKind.Error(CalculatorErrorType.ENTER_NUMBER_FIRST),
                )
            }
            try {
                val answer = ExpressionEvaluator.evaluate(expr)
                val formatted = ExpressionEvaluator.formatNumber(answer)
                val newEntry = HistoryEntry(
                    id = nextHistoryId++,
                    expression = expr,
                    result = formatted,
                )
                state.copy(
                    expression = formatted,
                    displayText = formatted,
                    livePreview = null,
                    statusKind = StatusKind.Success,
                    justCalculated = true,
                    history = (listOf(newEntry) + state.history).take(30),
                )
            } catch (e: CalculatorException) {
                state.copy(
                    expression = "",
                    displayText = "Error",
                    livePreview = null,
                    statusKind = StatusKind.Error(e.errorType),
                    justCalculated = false,
                )
            }
        }
    }

    fun applyScientific(operation: ScientificOperation) {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isBlank()) {
                return@update state.copy(
                    statusKind = StatusKind.Error(CalculatorErrorType.ENTER_NUMBER_FIRST),
                )
            }
            try {
                val answer = ExpressionEvaluator.applyScientific(expr, operation)
                val formatted = ExpressionEvaluator.formatNumber(answer)
                val historyLabel = when (operation) {
                    ScientificOperation.SIN -> "sin($expr)"
                    ScientificOperation.COS -> "cos($expr)"
                    ScientificOperation.TAN -> "tan($expr)"
                    ScientificOperation.SQRT -> "√($expr)"
                    ScientificOperation.LN -> "ln($expr)"
                    ScientificOperation.LOG -> "log($expr)"
                    ScientificOperation.FACTORIAL -> "($expr)!"
                    ScientificOperation.SQUARE -> "($expr)²"
                    ScientificOperation.SIGN -> "−($expr)"
                }
                val newEntry = HistoryEntry(
                    id = nextHistoryId++,
                    expression = historyLabel,
                    result = formatted,
                )
                state.copy(
                    expression = formatted,
                    displayText = formatted,
                    livePreview = null,
                    statusKind = StatusKind.ScientificSuccess(operation.label),
                    justCalculated = true,
                    history = (listOf(newEntry) + state.history).take(30),
                )
            } catch (e: CalculatorException) {
                state.copy(
                    livePreview = null,
                    statusKind = StatusKind.Error(e.errorType),
                )
            }
        }
    }

    fun useHistoryResult(result: String) {
        _uiState.update { state ->
            state.copy(
                expression = result,
                displayText = result,
                livePreview = null,
                statusKind = StatusKind.Ready,
                justCalculated = true,
            )
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    private fun updateCalculatingState(state: CalculatorUiState, nextExpr: String): CalculatorUiState {
        val preview = computeLivePreview(nextExpr)
        return state.copy(
            expression = nextExpr,
            displayText = nextExpr,
            livePreview = preview,
            statusKind = StatusKind.Calculating,
            justCalculated = false,
        )
    }

    private fun computeLivePreview(expr: String): String? {
        val hasOperatorOrConstant = expr.any {
            it == '+' || it == '−' || it == '-' || it == '×' || it == '*' ||
                it == '÷' || it == '/' || it == '^' || it == 'π' || it == 'e' || it == '('
        }
        if (!hasOperatorOrConstant) return null
        return try {
            val result = ExpressionEvaluator.evaluate(expr)
            val formatted = ExpressionEvaluator.formatNumber(result)
            if (formatted == expr) null else formatted
        } catch (_: CalculatorException) {
            null
        }
    }
}
