package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.core.util.formatResultNumber
import com.nuvexa.app.ui.theme.LocalSpacing
import com.nuvexa.app.ui.theme.NuvexaExtraType
import java.math.BigDecimal
import java.math.MathContext

private enum class Operator(val symbol: String) { ADD("+"), SUBTRACT("−"), MULTIPLY("×"), DIVIDE("÷") }

private data class CalculatorState(
    val display: String = "0",
    val operand: BigDecimal? = null,
    val operator: Operator? = null,
    val newInput: Boolean = true,
    val expression: String = "",
)

@Composable
fun BasicCalculatorScreen(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(CalculatorState()) }
    val spacing = LocalSpacing.current
    val divisionByZeroError = stringResource(R.string.error_division_by_zero)

    fun currentValue(): BigDecimal = state.display.toBigDecimalOrNull() ?: BigDecimal.ZERO

    // Returns null to signal "can't divide by zero" rather than silently producing a number.
    fun applyPending(): BigDecimal? {
        val left = state.operand ?: return currentValue()
        val right = currentValue()
        return when (state.operator) {
            Operator.ADD -> left + right
            Operator.SUBTRACT -> left - right
            Operator.MULTIPLY -> left * right
            Operator.DIVIDE -> if (right == BigDecimal.ZERO) null else left.divide(right, MathContext.DECIMAL64)
            null -> right
        }
    }

    fun onDigit(digit: String) {
        state = if (state.newInput) {
            state.copy(display = digit, newInput = false)
        } else if (state.display == "0" && digit != ".") {
            state.copy(display = digit)
        } else if (digit == "." && state.display.contains(".")) {
            state
        } else {
            state.copy(display = state.display + digit)
        }
    }

    fun onOperator(op: Operator) {
        val result = if (state.operator != null && !state.newInput) applyPending() else currentValue()
        if (result == null) {
            state = CalculatorState(display = divisionByZeroError, newInput = true)
            return
        }
        state = state.copy(
            display = formatResultNumber(result),
            operand = result,
            operator = op,
            newInput = true,
            expression = "${formatResultNumber(result)} ${op.symbol}",
        )
    }

    fun onEquals() {
        if (state.operator == null) return
        val result = applyPending()
        state = if (result == null) {
            CalculatorState(display = divisionByZeroError, newInput = true)
        } else {
            CalculatorState(display = formatResultNumber(result), newInput = true)
        }
    }

    fun onClear() {
        state = CalculatorState()
    }

    fun onToggleSign() {
        state = state.copy(display = formatResultNumber(currentValue().negate()))
    }

    fun onPercent() {
        state = state.copy(display = formatResultNumber(currentValue().divide(BigDecimal(100), MathContext.DECIMAL64)))
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = androidx.compose.ui.Alignment.End,
        ) {
            Text(
                text = state.expression,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = state.display,
                style = NuvexaExtraType.numericEmphasis,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }

        val buttonRows = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "="),
        )

        buttonRows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.s)) {
                row.forEach { key ->
                    val isWide = key == "0"
                    Button(
                        onClick = {
                            when (key) {
                                "C" -> onClear()
                                "±" -> onToggleSign()
                                "%" -> onPercent()
                                "÷" -> onOperator(Operator.DIVIDE)
                                "×" -> onOperator(Operator.MULTIPLY)
                                "−" -> onOperator(Operator.SUBTRACT)
                                "+" -> onOperator(Operator.ADD)
                                "=" -> onEquals()
                                "." -> onDigit(".")
                                else -> onDigit(key)
                            }
                        },
                        modifier = Modifier
                            .weight(if (isWide) 2f else 1f)
                            .aspectRatio(if (isWide) 2f else 1f)
                            .height(64.dp),
                        colors = when (key) {
                            "÷", "×", "−", "+", "=" -> ButtonDefaults.buttonColors()
                            "C", "±", "%" -> ButtonDefaults.filledTonalButtonColors()
                            else -> ButtonDefaults.elevatedButtonColors()
                        },
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text(key, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
