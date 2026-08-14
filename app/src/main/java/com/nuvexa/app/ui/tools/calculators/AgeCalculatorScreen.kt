package com.nuvexa.app.ui.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.DatePickerField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.time.LocalDate
import java.time.Period

@Composable
fun AgeCalculatorScreen(modifier: Modifier = Modifier, onResult: (String) -> Unit) {
    val spacing = LocalSpacing.current
    var birthDate by remember { mutableStateOf(LocalDate.now().minusYears(20)) }
    var age by remember { mutableStateOf<Period?>(null) }
    val resultText = age?.let { stringResource(R.string.label_age_result, it.years, it.months, it.days) }

    fun calculate() {
        age = Period.between(birthDate, LocalDate.now())
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        DatePickerField(
            label = stringResource(R.string.label_date_of_birth),
            selectedDate = birthDate,
            onDateSelected = { birthDate = it; age = null },
        )
        PrimaryButton(
            text = stringResource(R.string.action_calculate),
            onClick = {
                calculate()
                age?.let { onResult(resultForHistory(it)) }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        resultText?.let { ResultCard(value = it, valueStyle = androidx.compose.material3.MaterialTheme.typography.headlineMedium) }
    }
}

private fun resultForHistory(period: Period): String = "${period.years}y ${period.months}m ${period.days}d"
