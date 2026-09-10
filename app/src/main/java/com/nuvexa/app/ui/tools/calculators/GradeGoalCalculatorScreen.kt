package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.text.DecimalFormat

@Composable
fun GradeGoalCalculatorScreen(
    modifier: Modifier = Modifier,
    onHistory: (String) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    var currentAverage by remember { mutableStateOf("") }
    var completedWeight by remember { mutableStateOf("") }
    var targetGrade by remember { mutableStateOf("") }

    val current = currentAverage.toDoubleOrNull()
    val completed = completedWeight.toDoubleOrNull()
    val target = targetGrade.toDoubleOrNull()
    val valid = current != null && completed != null && target != null &&
        current in 0.0..100.0 && target in 0.0..100.0 && completed > 0.0 && completed < 100.0

    val required = if (valid) {
        val remaining = 100.0 - completed!!
        (target!! * 100.0 - current!! * completed) / remaining
    } else null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.l),
    ) {
        NuvexaNumberField(
            value = currentAverage,
            onValueChange = { currentAverage = it },
            label = stringResource(R.string.grade_goal_current),
            suffix = "%",
        )
        NuvexaNumberField(
            value = completedWeight,
            onValueChange = { completedWeight = it },
            label = stringResource(R.string.grade_goal_completed),
            suffix = "%",
        )
        NuvexaNumberField(
            value = targetGrade,
            onValueChange = { targetGrade = it },
            label = stringResource(R.string.grade_goal_target),
            suffix = "%",
        )

        if (currentAverage.isNotBlank() && completedWeight.isNotBlank() && targetGrade.isNotBlank()) {
            if (!valid || required == null || !required.isFinite()) {
                Text(
                    text = stringResource(R.string.grade_goal_invalid),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                val remainingText = DecimalFormat("0.##").format(100.0 - completed!!)
                val requiredText = DecimalFormat("0.##").format(required.coerceAtLeast(0.0))
                ResultCard(
                    value = "$requiredText%",
                    label = stringResource(R.string.grade_goal_required, remainingText),
                )
                when {
                    required > 100.0 -> Text(
                        text = stringResource(R.string.grade_goal_impossible),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    required <= 0.0 -> Text(
                        text = stringResource(R.string.grade_goal_secured),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                androidx.compose.runtime.LaunchedEffect(current, completed, target, required) {
                    onHistory("Target ${DecimalFormat("0.##").format(target)}% → need $requiredText% on remaining $remainingText%")
                }
            }
        }
    }
}
