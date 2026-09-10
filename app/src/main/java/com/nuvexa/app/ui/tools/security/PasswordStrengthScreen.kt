package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.nuvexa.app.R
import com.nuvexa.app.ui.theme.LocalSpacing

@Composable
fun PasswordStrengthScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var password by remember { mutableStateOf("") }
    val assessment = remember(password) { assessPassword(password) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_strength_input)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.medium,
        )

        if (password.isNotEmpty()) {
            Text(
                text = stringResource(assessment.labelRes),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            LinearProgressIndicator(
                progress = assessment.score / 5f,
                modifier = Modifier.fillMaxWidth(),
            )

            RequirementRow(assessment.hasLength, stringResource(R.string.password_strength_length))
            RequirementRow(assessment.hasMixedCase, stringResource(R.string.password_strength_case))
            RequirementRow(assessment.hasNumber, stringResource(R.string.password_strength_number))
            RequirementRow(assessment.hasSymbol, stringResource(R.string.password_strength_symbol))
            RequirementRow(assessment.hasExtraStrength, stringResource(R.string.password_strength_extra))
        }

        Text(
            text = stringResource(R.string.password_strength_privacy),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RequirementRow(satisfied: Boolean, label: String) {
    val spacing = LocalSpacing.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.s),
    ) {
        Icon(
            imageVector = if (satisfied) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (satisfied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (satisfied) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private data class PasswordAssessment(
    val score: Int,
    val labelRes: Int,
    val hasLength: Boolean,
    val hasMixedCase: Boolean,
    val hasNumber: Boolean,
    val hasSymbol: Boolean,
    val hasExtraStrength: Boolean,
)

private fun assessPassword(password: String): PasswordAssessment {
    val hasLength = password.length >= 12
    val hasMixedCase = password.any(Char::isUpperCase) && password.any(Char::isLowerCase)
    val hasNumber = password.any(Char::isDigit)
    val hasSymbol = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    val varietyRatio = if (password.isEmpty()) 0f else password.toSet().size.toFloat() / password.length
    val hasExtraStrength = password.length >= 16 || (password.length >= 12 && varietyRatio >= 0.65f)
    val score = listOf(hasLength, hasMixedCase, hasNumber, hasSymbol, hasExtraStrength).count { it }
    val labelRes = when (score) {
        0, 1 -> R.string.password_strength_weak
        2 -> R.string.password_strength_fair
        3 -> R.string.password_strength_good
        4 -> R.string.password_strength_strong
        else -> R.string.password_strength_excellent
    }
    return PasswordAssessment(score, labelRes, hasLength, hasMixedCase, hasNumber, hasSymbol, hasExtraStrength)
}
