package com.nuvexa.app.ui.tools.security

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
import com.nuvexa.app.ui.components.NuvexaNumberField
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.components.ResultCard
import com.nuvexa.app.ui.theme.LocalSpacing
import java.security.SecureRandom

@Composable
fun RandomNumberScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    var min by remember { mutableStateOf("1") }
    var max by remember { mutableStateOf("100") }
    var result by remember { mutableStateOf<Int?>(null) }

    fun generate() {
        val minValue = min.toIntOrNull() ?: return
        val maxValue = max.toIntOrNull() ?: return
        if (minValue > maxValue) return
        result = minValue + SecureRandom().nextInt(maxValue - minValue + 1)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        NuvexaNumberField(value = min, onValueChange = { min = it; result = null }, label = stringResource(R.string.random_number_min), allowNegative = true, allowDecimal = false)
        NuvexaNumberField(value = max, onValueChange = { max = it; result = null }, label = stringResource(R.string.random_number_max), allowNegative = true, allowDecimal = false)
        PrimaryButton(text = stringResource(R.string.action_generate), onClick = ::generate, modifier = Modifier.fillMaxWidth())
        result?.let { ResultCard(value = it.toString()) }
    }
}
