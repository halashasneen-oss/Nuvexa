package com.nuvexa.app.ui.tools.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nuvexa.app.R
import com.nuvexa.app.ui.theme.LocalNuvexaStatusColors
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlin.math.log2

private data class PasswordAudit(
    val level: Int,
    val entropyBits: Int,
    val checks: List<Boolean>,
)

@Composable
fun PasswordStrengthScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val status = LocalNuvexaStatusColors.current
    var password by remember { mutableStateOf("") }
    var reveal by remember { mutableStateOf(false) }
    val audit = remember(password) { auditPassword(password) }
    val levelColor: Color = when (audit.level) {
        1 -> MaterialTheme.colorScheme.error
        2 -> status.warning
        3 -> status.info
        4 -> status.success
        else -> MaterialTheme.colorScheme.outline
    }
    val levelLabel = when (audit.level) {
        1 -> stringResource(R.string.password_audit_weak)
        2 -> stringResource(R.string.password_audit_fair)
        3 -> stringResource(R.string.password_audit_good)
        4 -> stringResource(R.string.password_audit_strong)
        else -> stringResource(R.string.password_audit_enter_password)
    }

    val checkLabels = listOf(
        stringResource(R.string.password_audit_length),
        stringResource(R.string.password_audit_uppercase),
        stringResource(R.string.password_audit_lowercase),
        stringResource(R.string.password_audit_number),
        stringResource(R.string.password_audit_symbol),
        stringResource(R.string.password_audit_no_common),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password_audit_password)) },
            singleLine = true,
            visualTransformation = if (reveal) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { reveal = !reveal }) {
                    Icon(
                        imageVector = if (reveal) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = null,
                    )
                }
            },
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier.padding(spacing.m),
                verticalArrangement = Arrangement.spacedBy(spacing.m),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(levelLabel, style = MaterialTheme.typography.titleMedium, color = levelColor)
                    if (password.isNotEmpty()) {
                        Text(
                            stringResource(R.string.password_audit_entropy, audit.entropyBits),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(7.dp)
                                .background(
                                    color = if (index < audit.level) levelColor else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(99.dp),
                                ),
                        )
                    }
                }

                if (password.isNotEmpty()) {
                    checkLabels.forEachIndexed { index, label ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.s),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = if (audit.checks[index]) status.success else MaterialTheme.colorScheme.outline,
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (audit.checks[index]) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.password_audit_privacy_note),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun auditPassword(password: String): PasswordAudit {
    if (password.isEmpty()) return PasswordAudit(level = 0, entropyBits = 0, checks = List(6) { false })

    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSymbol = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    val commonPattern = Regex("password|qwerty|admin|letmein|1234|0000", RegexOption.IGNORE_CASE).containsMatchIn(password)
    val checks = listOf(
        password.length >= 12,
        hasUpper,
        hasLower,
        hasDigit,
        hasSymbol,
        !commonPattern,
    )

    var pool = 0
    if (hasLower) pool += 26
    if (hasUpper) pool += 26
    if (hasDigit) pool += 10
    if (hasSymbol) pool += 32
    val entropy = if (pool > 0) (password.length * log2(pool.toDouble())).toInt() else 0

    var score = checks.count { it }
    if (password.length >= 16) score++
    if (entropy >= 60) score++
    if (commonPattern) score -= 2

    val level = when {
        score <= 2 -> 1
        score <= 4 -> 2
        score <= 6 -> 3
        else -> 4
    }
    return PasswordAudit(level = level, entropyBits = entropy, checks = checks)
}
