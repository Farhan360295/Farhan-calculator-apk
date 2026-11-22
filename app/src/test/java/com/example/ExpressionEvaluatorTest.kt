package com.example

import com.example.calculator.CalculatorErrorType
import com.example.calculator.CalculatorException
import com.example.calculator.ExpressionEvaluator
import com.example.calculator.ScientificOperation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExpressionEvaluatorTest {

    @Test
    fun `basic arithmetic respects operator precedence and parentheses`() {
        assertEquals("14", ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("2+3×4")))
        assertEquals("20", ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("(2+3)×4")))
        assertEquals("2.5", ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("(7−2)÷2")))
        assertEquals("0.3", ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("0.1+0.2")))
    }

    @Test
    fun `powers constants and implicit multiplication work accurately`() {
        assertEquals("512", ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("2^3^2")))
        assertEquals("10", ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("2(3+2)")))
        val twoPi = ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("2π"))
        assertEquals("6.28318530718", twoPi)
        val ePlusOne = ExpressionEvaluator.formatNumber(ExpressionEvaluator.evaluate("e+1"))
        assertEquals("3.71828182846", ePlusOne)
    }

    @Test
    fun `degree trigonometry and undefined tangent behave correctly`() {
        assertEquals("0.5", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("30", ScientificOperation.SIN)))
        assertEquals("0.5", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("60", ScientificOperation.COS)))
        assertEquals("1", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("45", ScientificOperation.TAN)))

        val ex = assertThrows(CalculatorException::class.java) {
            ExpressionEvaluator.applyScientific("90", ScientificOperation.TAN)
        }
        assertEquals(CalculatorErrorType.TANGENT_UNDEFINED, ex.errorType)
    }

    @Test
    fun `scientific functions and domain errors work properly`() {
        assertEquals("9", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("81", ScientificOperation.SQRT)))
        assertEquals("2", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("100", ScientificOperation.LOG)))
        assertEquals("1", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("e", ScientificOperation.LN)))
        assertEquals("120", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("5", ScientificOperation.FACTORIAL)))
        assertEquals("144", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("12", ScientificOperation.SQUARE)))
        assertEquals("-42", ExpressionEvaluator.formatNumber(ExpressionEvaluator.applyScientific("42", ScientificOperation.SIGN)))

        val divZero = assertThrows(CalculatorException::class.java) {
            ExpressionEvaluator.evaluate("5÷0")
        }
        assertEquals(CalculatorErrorType.DIVISION_BY_ZERO, divZero.errorType)

        val negSqrt = assertThrows(CalculatorException::class.java) {
            ExpressionEvaluator.applyScientific("-4", ScientificOperation.SQRT)
        }
        assertEquals(CalculatorErrorType.INVALID_SQRT, negSqrt.errorType)

        val badFact = assertThrows(CalculatorException::class.java) {
            ExpressionEvaluator.applyScientific("2.5", ScientificOperation.FACTORIAL)
        }
        assertEquals(CalculatorErrorType.FACTORIAL_RANGE, badFact.errorType)
    }
}
