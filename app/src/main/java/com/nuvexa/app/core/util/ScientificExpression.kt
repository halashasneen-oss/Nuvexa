package com.nuvexa.app.core.util

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/** Small offline scientific-expression parser used by the scientific calculator. */
object ScientificExpression {
    fun evaluate(expression: String, useDegrees: Boolean = true): Double {
        require(expression.isNotBlank()) { "Expression is empty" }
        val normalized = expression
            .map { original ->
                when (original) {
                    in '٠'..'٩' -> ('0'.code + (original.code - '٠'.code)).toChar()
                    in '۰'..'۹' -> ('0'.code + (original.code - '۰'.code)).toChar()
                    '×' -> '*'
                    '÷' -> '/'
                    '−', '–', '—' -> '-'
                    '٫', ',' -> '.'
                    else -> original
                }
            }
            .joinToString("")
            .replace("π", "pi")

        val value = Parser(normalized, useDegrees).parse()
        require(value.isFinite()) { "Result is not finite" }
        return if (abs(value) < 1e-12) 0.0 else value
    }

    private class Parser(
        private val source: String,
        private val useDegrees: Boolean,
    ) {
        private var position = 0

        fun parse(): Double {
            val value = parseExpression()
            skipWhitespace()
            require(position == source.length) { "Unexpected input at position $position" }
            return value
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                value = when {
                    match('+') -> value + parseTerm()
                    match('-') -> value - parseTerm()
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parseUnary()
            while (true) {
                when {
                    match('*') -> value *= parseUnary()
                    match('/') -> {
                        val divisor = parseUnary()
                        require(divisor != 0.0) { "Division by zero" }
                        value /= divisor
                    }
                    else -> return value
                }
            }
        }

        private fun parseUnary(): Double = when {
            match('+') -> parseUnary()
            match('-') -> -parseUnary()
            else -> parsePower()
        }

        private fun parsePower(): Double {
            val base = parsePrimary()
            return if (match('^')) base.pow(parseUnary()) else base
        }

        private fun parsePrimary(): Double {
            skipWhitespace()
            if (match('(')) {
                val value = parseExpression()
                require(match(')')) { "Missing closing parenthesis" }
                return value
            }

            val current = peek()
            return when {
                current == null -> throw IllegalArgumentException("Unexpected end of expression")
                current.isDigit() || current == '.' -> parseNumber()
                current.isLetter() -> parseIdentifierOrFunction()
                else -> throw IllegalArgumentException("Unexpected '$current' at position $position")
            }
        }

        private fun parseNumber(): Double {
            skipWhitespace()
            val start = position
            var seenDigit = false
            var seenDot = false

            while (position < source.length) {
                val c = source[position]
                when {
                    c.isDigit() -> {
                        seenDigit = true
                        position++
                    }
                    c == '.' && !seenDot -> {
                        seenDot = true
                        position++
                    }
                    else -> break
                }
            }

            require(seenDigit) { "Invalid number" }

            if (position < source.length && (source[position] == 'e' || source[position] == 'E')) {
                val exponentMarker = position
                position++
                if (position < source.length && (source[position] == '+' || source[position] == '-')) position++
                val exponentStart = position
                while (position < source.length && source[position].isDigit()) position++
                if (exponentStart == position) position = exponentMarker
            }

            return source.substring(start, position).toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid number")
        }

        private fun parseIdentifierOrFunction(): Double {
            skipWhitespace()
            val start = position
            while (position < source.length && source[position].isLetter()) position++
            val name = source.substring(start, position).lowercase()

            if (name == "pi") return Math.PI
            if (name == "e") return Math.E

            require(match('(')) { "Function '$name' requires parentheses" }
            val argument = parseExpression()
            require(match(')')) { "Missing closing parenthesis after '$name'" }

            val angle = if (useDegrees) Math.toRadians(argument) else argument
            val result = when (name) {
                "sin" -> sin(angle)
                "cos" -> cos(angle)
                "tan" -> tan(angle)
                "asin" -> asin(argument).let { if (useDegrees) Math.toDegrees(it) else it }
                "acos" -> acos(argument).let { if (useDegrees) Math.toDegrees(it) else it }
                "atan" -> atan(argument).let { if (useDegrees) Math.toDegrees(it) else it }
                "sqrt" -> sqrt(argument)
                "log" -> log10(argument)
                "ln" -> ln(argument)
                "abs" -> abs(argument)
                "floor" -> floor(argument)
                "ceil" -> ceil(argument)
                else -> throw IllegalArgumentException("Unknown function '$name'")
            }
            require(result.isFinite()) { "Invalid function result" }
            return result
        }

        private fun match(expected: Char): Boolean {
            skipWhitespace()
            if (position < source.length && source[position] == expected) {
                position++
                return true
            }
            return false
        }

        private fun peek(): Char? {
            skipWhitespace()
            return source.getOrNull(position)
        }

        private fun skipWhitespace() {
            while (position < source.length && source[position].isWhitespace()) position++
        }
    }
}
