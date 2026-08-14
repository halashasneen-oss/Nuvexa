package com.nuvexa.app.ui.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.ui.components.ToolScaffold
import com.nuvexa.app.ui.tools.calculators.AgeCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.BasicCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.CompoundInterestScreen
import com.nuvexa.app.ui.tools.calculators.DateDifferenceScreen
import com.nuvexa.app.ui.tools.calculators.DiscountCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.EmiCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.PercentageCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.SimpleInterestScreen
import com.nuvexa.app.ui.tools.calculators.SplitBillScreen
import com.nuvexa.app.ui.tools.calculators.TipCalculatorScreen
import com.nuvexa.app.ui.tools.color.ColorConverterScreen
import com.nuvexa.app.ui.tools.color.PaletteGeneratorScreen
import com.nuvexa.app.ui.tools.converter.UnitConverterScreen
import com.nuvexa.app.ui.tools.currency.CurrencyCalculatorScreen
import com.nuvexa.app.ui.tools.developer.LoremIpsumScreen
import com.nuvexa.app.ui.tools.developer.RegexTesterScreen
import com.nuvexa.app.ui.tools.device.DeviceInfoScreen
import com.nuvexa.app.ui.tools.image.ImageCompressorScreen
import com.nuvexa.app.ui.tools.image.ImageResizerScreen
import com.nuvexa.app.ui.tools.qr.QrGeneratorScreen
import com.nuvexa.app.ui.tools.qr.QrScannerScreen
import com.nuvexa.app.ui.tools.security.HashGeneratorScreen
import com.nuvexa.app.ui.tools.security.PasswordGeneratorScreen
import com.nuvexa.app.ui.tools.security.PinGeneratorScreen
import com.nuvexa.app.ui.tools.security.RandomNumberScreen
import com.nuvexa.app.ui.tools.security.UuidGeneratorScreen
import com.nuvexa.app.ui.tools.text.Base64ToolScreen
import com.nuvexa.app.ui.tools.text.CaseConverterScreen
import com.nuvexa.app.ui.tools.text.JsonFormatterScreen
import com.nuvexa.app.ui.tools.text.TextAnalyzerScreen
import com.nuvexa.app.ui.tools.text.TextCleanerScreen
import com.nuvexa.app.ui.tools.text.UrlEncoderScreen
import com.nuvexa.app.ui.tools.time.DateCalculatorScreen
import com.nuvexa.app.ui.tools.time.StopwatchScreen
import com.nuvexa.app.ui.tools.time.TimerScreen
import com.nuvexa.app.ui.tools.time.UnixTimestampScreen
import com.nuvexa.app.ui.tools.time.WorldClockScreen

/**
 * Dispatches a [Tool] id to its real screen composable and wraps it in the shared
 * [ToolScaffold] chrome (back, favorite toggle, usage tracking). Every id in
 * [com.nuvexa.app.core.registry.ToolRegistry] must have a branch here — that invariant is
 * what keeps the registry honest: nothing is listed unless it actually opens to a working
 * screen.
 */
@Composable
fun ToolScreenHost(tool: Tool, onBack: () -> Unit) {
    val chromeViewModel: ToolChromeViewModel = hiltViewModel()
    chromeViewModel.bind(tool.id)
    val isFavorite by chromeViewModel.isFavorite.collectAsStateWithLifecycle()
    val recordHistory: (String) -> Unit = chromeViewModel::recordHistory

    ToolScaffold(
        tool = tool,
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = chromeViewModel::toggleFavorite,
    ) { modifier ->
        when (tool.id) {
            "calc_basic" -> BasicCalculatorScreen(modifier)
            "calc_percentage" -> PercentageCalculatorScreen(modifier, recordHistory)
            "calc_discount" -> DiscountCalculatorScreen(modifier, recordHistory)
            "calc_tip" -> TipCalculatorScreen(modifier, recordHistory)
            "calc_split_bill" -> SplitBillScreen(modifier, recordHistory)
            "calc_age" -> AgeCalculatorScreen(modifier, recordHistory)
            "calc_date_diff" -> DateDifferenceScreen(modifier, recordHistory)
            "calc_simple_interest" -> SimpleInterestScreen(modifier, recordHistory)
            "calc_compound_interest" -> CompoundInterestScreen(modifier, recordHistory)
            "calc_emi" -> EmiCalculatorScreen(modifier, recordHistory)
            "unit_converter" -> UnitConverterScreen(modifier)
            "currency_calculator" -> CurrencyCalculatorScreen(modifier)
            "text_analyzer" -> TextAnalyzerScreen(modifier)
            "text_case_converter" -> CaseConverterScreen(modifier)
            "text_cleaner" -> TextCleanerScreen(modifier)
            "text_base64" -> Base64ToolScreen(modifier)
            "text_url_encode" -> UrlEncoderScreen(modifier)
            "text_json_formatter" -> JsonFormatterScreen(modifier)
            "sec_password_generator" -> PasswordGeneratorScreen(modifier)
            "sec_pin_generator" -> PinGeneratorScreen(modifier)
            "sec_uuid_generator" -> UuidGeneratorScreen(modifier)
            "sec_random_number" -> RandomNumberScreen(modifier)
            "sec_hash_generator" -> HashGeneratorScreen(modifier)
            "qr_generator" -> QrGeneratorScreen(modifier, recordHistory)
            "qr_scanner" -> QrScannerScreen(modifier, recordHistory)
            "color_converter" -> ColorConverterScreen(modifier)
            "color_palette_generator" -> PaletteGeneratorScreen(modifier)
            "time_world_clock" -> WorldClockScreen(modifier)
            "time_stopwatch" -> StopwatchScreen(modifier)
            "time_timer" -> TimerScreen(modifier)
            "time_unix_converter" -> UnixTimestampScreen(modifier)
            "time_date_calculator" -> DateCalculatorScreen(modifier)
            "dev_regex_tester" -> RegexTesterScreen(modifier)
            "dev_lorem_ipsum" -> LoremIpsumScreen(modifier)
            "device_info" -> DeviceInfoScreen(modifier)
            "image_compressor" -> ImageCompressorScreen(modifier, recordHistory)
            "image_resizer" -> ImageResizerScreen(modifier, recordHistory)
        }
    }
}
