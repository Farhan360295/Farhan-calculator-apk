package com.example.calculator

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

enum class CalculatorErrorType {
    INVALID_EXPRESSION,
    DIVISION_BY_ZERO,
    INVALID_RESULT,
    ENTER_NUMBER_FIRST,
    TANGENT_UNDEFINED,
    INVALID_SQRT,
    POSITIVE_NUMBER_REQUIRED,
    FACTORIAL_RANGE,
}

class CalculatorException(val errorType: CalculatorErrorType) : Exception(errorType.name)

enum class ScientificOperation(val label: String) {
    SIN("SIN"),
    COS("COS"),
    TAN("TAN"),
    SQRT("SQRT"),
    LN("LN"),
    LOG("LOG"),
    FACTORIAL("FACTORIAL"),
    SQUARE("SQUARE"),
    SIGN("SIGN"),
}

object ExpressionEvaluator {

    private sealed interface Token {
        data class Number(val value: Double) : Token
        data class Op(val symbol: Char) : Token
        data object LParen : Token
        data object RParen : Token
    }

    fun evaluate(rawExpression: String): Double {
        val expr = rawExpression.trim()
        if (expr.isEmpty()) {
            throw CalculatorException(CalculatorErrorType.ENTER_NUMBER_FIRST)
        }

        val tokens = tokenize(expr)
        if (tokens.isEmpty()) {
            throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
        }

        val parser = Parser(tokens)
        val result = parser.parseExpression()
        if (!parser.isAtEnd()) {
            throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
        }
        if (!result.isFinite()) {
            throw CalculatorException(CalculatorErrorType.INVALID_RESULT)
        }
        return result
    }

    fun applyScientific(rawExpression: String, operation: ScientificOperation): Double {
        val expr = rawExpression.trim()
        if (expr.isEmpty()) {
            throw CalculatorException(CalculatorErrorType.ENTER_NUMBER_FIRST)
        }

        val number = evaluate(expr)
        val answer = when (operation) {
            ScientificOperation.SIN -> sin(number * PI / 180.0)
            ScientificOperation.COS -> cos(number * PI / 180.0)
            ScientificOperation.TAN -> {
                val rad = number * PI / 180.0
                if (abs(cos(rad)) < 1e-12) {
                    throw CalculatorException(CalculatorErrorType.TANGENT_UNDEFINED)
                }
                tan(rad)
            }
            ScientificOperation.SQRT -> {
                if (number < 0.0) {
                    throw CalculatorException(CalculatorErrorType.INVALID_SQRT)
                }
                sqrt(number)
            }
            ScientificOperation.LN -> {
                if (number <= 0.0) {
                    throw CalculatorException(CalculatorErrorType.POSITIVE_NUMBER_REQUIRED)
                }
                ln(number)
            }
            ScientificOperation.LOG -> {
                if (number <= 0.0) {
                    throw CalculatorException(CalculatorErrorType.POSITIVE_NUMBER_REQUIRED)
                }
                log10(number)
            }
            ScientificOperation.SQUARE -> number * number
            ScientificOperation.SIGN -> -number
            ScientificOperation.FACTORIAL -> {
                if (!number.isFinite() || number < 0.0 || number > 170.0 || number % 1.0 != 0.0) {
                    throw CalculatorException(CalculatorErrorType.FACTORIAL_RANGE)
                }
                val n = number.toInt()
                var fact = 1.0
                for (i in 2..n) {
                    fact *= i
                }
                fact
            }
        }

        if (!answer.isFinite()) {
            throw CalculatorException(CalculatorErrorType.INVALID_RESULT)
        }
        return answer
    }

    fun formatNumber(value: Double): String {
        if (!value.isFinite()) {
            throw CalculatorException(CalculatorErrorType.INVALID_RESULT)
        }
        val normalized = if (abs(value) < 1e-14) 0.0 else value
        if (normalized == 0.0) return "0"

        val bd = BigDecimal.valueOf(normalized)
            .round(MathContext(12))
            .stripTrailingZeros()

        val absVal = abs(normalized)
        return if (absVal >= 1e12 || absVal < 1e-9) {
            bd.toString()
        } else {
            bd.toPlainString()
        }
    }

    private fun tokenize(input: String): List<Token> {
        val rawTokens = mutableListOf<Token>()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val start = i
                    var dotCount = 0
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) {
                        if (input[i] == '.') dotCount++
                        i++
                    }
                    // Support scientific notation output like 1.5E-4 or 1.2E+15 (uppercase E only; lowercase e is Euler's constant)
                    if (i < input.length && input[i] == 'E') {
                        val nextIdx = i + 1
                        val hasSign = nextIdx < input.length && (input[nextIdx] == '+' || input[nextIdx] == '-')
                        val digitStart = if (hasSign) nextIdx + 1 else nextIdx
                        if (digitStart < input.length && input[digitStart].isDigit()) {
                            i = digitStart
                            while (i < input.length && input[i].isDigit()) {
                                i++
                            }
                        }
                    }
                    if (dotCount > 1) {
                        throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
                    }
                    val numStr = input.substring(start, i)
                    val parsed = numStr.toDoubleOrNull()
                        ?: throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
                    rawTokens.add(Token.Number(parsed))
                }
                c == 'π' -> {
                    rawTokens.add(Token.Number(Math.PI))
                    i++
                }
                c == 'e' -> {
                    rawTokens.add(Token.Number(Math.E))
                    i++
                }
                c == '(' -> {
                    rawTokens.add(Token.LParen)
                    i++
                }
                c == ')' -> {
                    rawTokens.add(Token.RParen)
                    i++
                }
                c == '+' -> {
                    rawTokens.add(Token.Op('+'))
                    i++
                }
                c == '-' || c == '−' -> {
                    rawTokens.add(Token.Op('-'))
                    i++
                }
                c == '*' || c == '×' -> {
                    rawTokens.add(Token.Op('*'))
                    i++
                }
                c == '/' || c == '÷' -> {
                    rawTokens.add(Token.Op('/'))
                    i++
                }
                c == '^' -> {
                    rawTokens.add(Token.Op('^'))
                    i++
                }
                else -> throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
            }
        }

        // Insert implicit multiplication where appropriate:
        // Number followed by LParen or Number (e.g. 2π or 2(3))
        // RParen followed by LParen or Number (e.g. (2)(3) or (2)3)
        val result = mutableListOf<Token>()
        for (idx in rawTokens.indices) {
            val current = rawTokens[idx]
            if (idx > 0) {
                val prev = rawTokens[idx - 1]
                val needsImplicitMul =
                    (prev is Token.Number && (current is Token.LParen || current is Token.Number)) ||
                        (prev is Token.RParen && (current is Token.LParen || current is Token.Number))
                if (needsImplicitMul) {
                    result.add(Token.Op('*'))
                }
            }
            result.add(current)
        }
        return result
    }

    private class Parser(private val tokens: List<Token>) {
        private var pos = 0

        fun isAtEnd(): Boolean = pos >= tokens.size

        private fun peek(): Token? = tokens.getOrNull(pos)

        private fun advance(): Token = tokens[pos++]

        fun parseExpression(): Double {
            var left = parseTerm()
            while (true) {
                val token = peek()
                if (token is Token.Op && (token.symbol == '+' || token.symbol == '-')) {
                    advance()
                    val right = parseTerm()
                    left = if (token.symbol == '+') left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseTerm(): Double {
            var left = parsePower()
            while (true) {
                val token = peek()
                if (token is Token.Op && (token.symbol == '*' || token.symbol == '/')) {
                    advance()
                    val right = parsePower()
                    if (token.symbol == '*') {
                        left *= right
                    } else {
                        if (right == 0.0) {
                            throw CalculatorException(CalculatorErrorType.DIVISION_BY_ZERO)
                        }
                        left /= right
                    }
                } else {
                    break
                }
            }
            return left
        }

        private fun parsePower(): Double {
            val base = parseUnary()
            val token = peek()
            return if (token is Token.Op && token.symbol == '^') {
                advance()
                val exponent = parsePower() // Right-associative
                base.pow(exponent)
            } else {
                base
            }
        }

        private fun parseUnary(): Double {
            val token = peek()
            if (token is Token.Op && (token.symbol == '+' || token.symbol == '-')) {
                advance()
                val operand = parseUnary()
                return if (token.symbol == '-') -operand else operand
            }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val token = peek() ?: throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
            return when (token) {
                is Token.Number -> {
                    advance()
                    token.value
                }
                Token.LParen -> {
                    advance()
                    val value = parseExpression()
                    if (peek() != Token.RParen) {
                        throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
                    }
                    advance()
                    value
                }
                else -> throw CalculatorException(CalculatorErrorType.INVALID_EXPRESSION)
            }
        }
    }
}
