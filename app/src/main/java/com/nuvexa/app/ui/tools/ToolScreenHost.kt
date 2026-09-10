package com.nuvexa.app.ui.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.ui.components.ToolScaffold
import com.nuvexa.app.ui.tools.calculators.AgeCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.AverageCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.BasicCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.CompoundInterestScreen
import com.nuvexa.app.ui.tools.calculators.DateDifferenceScreen
import com.nuvexa.app.ui.tools.calculators.DiscountCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.EmiCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.FractionCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.FuelTripCostScreen
import com.nuvexa.app.ui.tools.calculators.MarkupCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.MortgageCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.PercentageCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.ProfitMarginCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.RatioCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.SavingsCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.ScientificCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.SimpleInterestScreen
import com.nuvexa.app.ui.tools.calculators.SplitBillScreen
import com.nuvexa.app.ui.tools.calculators.TipCalculatorScreen
import com.nuvexa.app.ui.tools.calculators.WorkHoursCalculatorScreen
import com.nuvexa.app.ui.tools.color.ColorConverterScreen
import com.nuvexa.app.ui.tools.color.PaletteGeneratorScreen
import com.nuvexa.app.ui.tools.converter.UnitConverterScreen
import com.nuvexa.app.ui.tools.currency.CurrencyCalculatorScreen
import com.nuvexa.app.ui.tools.developer.CssFormatterScreen
import com.nuvexa.app.ui.tools.developer.HtmlFormatterScreen
import com.nuvexa.app.ui.tools.developer.JwtDecoderScreen
import com.nuvexa.app.ui.tools.developer.RegexTesterScreen
import com.nuvexa.app.ui.tools.developer.XmlFormatterScreen
import com.nuvexa.app.ui.tools.device.DeviceInfoScreen
import com.nuvexa.app.ui.tools.image.ImageCompressorScreen
import com.nuvexa.app.ui.tools.image.ImageResizerScreen
import com.nuvexa.app.ui.tools.math.BinaryConverterScreen
import com.nuvexa.app.ui.tools.math.FactorialScreen
import com.nuvexa.app.ui.tools.math.FibonacciScreen
import com.nuvexa.app.ui.tools.math.GcdLcmScreen
import com.nuvexa.app.ui.tools.math.PrimeCheckerScreen
import com.nuvexa.app.ui.tools.math.PrimeGeneratorScreen
import com.nuvexa.app.ui.tools.math.StatisticsScreen
import com.nuvexa.app.ui.tools.network.SubnetCalculatorScreen
import com.nuvexa.app.ui.tools.ocr.OcrFromCameraScreen
import com.nuvexa.app.ui.tools.ocr.OcrFromImageScreen
import com.nuvexa.app.ui.tools.ocr.OcrFromPdfScreen
import com.nuvexa.app.ui.tools.pdf.ImagesToPdfScreen
import com.nuvexa.app.ui.tools.pdf.PdfInspectorScreen
import com.nuvexa.app.ui.tools.pdf.PdfMergeScreen
import com.nuvexa.app.ui.tools.pdf.PdfOrganizeScreen
import com.nuvexa.app.ui.tools.pdf.PdfRotateScreen
import com.nuvexa.app.ui.tools.pdf.PdfSplitScreen
import com.nuvexa.app.ui.tools.pdf.PdfToImagesScreen
import com.nuvexa.app.ui.tools.pdf.PdfViewerScreen
import com.nuvexa.app.ui.tools.pdf.PdfWatermarkScreen
import com.nuvexa.app.ui.tools.pdf.TextToPdfScreen
import com.nuvexa.app.ui.tools.qr.QrGeneratorScreen
import com.nuvexa.app.ui.tools.qr.QrScannerScreen
import com.nuvexa.app.ui.tools.security.FileChecksumScreen
import com.nuvexa.app.ui.tools.security.HashGeneratorScreen
import com.nuvexa.app.ui.tools.security.HmacGeneratorScreen
import com.nuvexa.app.ui.tools.security.PassphraseGeneratorScreen
import com.nuvexa.app.ui.tools.security.PasswordGeneratorScreen
import com.nuvexa.app.ui.tools.security.PasswordStrengthScreen
import com.nuvexa.app.ui.tools.security.PinGeneratorScreen
import com.nuvexa.app.ui.tools.security.RandomNumberScreen
import com.nuvexa.app.ui.tools.security.TextEncryptionScreen
import com.nuvexa.app.ui.tools.security.UuidGeneratorScreen
import com.nuvexa.app.ui.tools.text.Base64ToolScreen
import com.nuvexa.app.ui.tools.text.CaseConverterScreen
import com.nuvexa.app.ui.tools.text.ExtractEmailsScreen
import com.nuvexa.app.ui.tools.text.ExtractNumbersScreen
import com.nuvexa.app.ui.tools.text.ExtractUrlsScreen
import com.nuvexa.app.ui.tools.text.JsonFormatterScreen
import com.nuvexa.app.ui.tools.text.TextAnalyzerScreen
import com.nuvexa.app.ui.tools.text.TextCleanerScreen
import com.nuvexa.app.ui.tools.text.UrlEncoderScreen
import com.nuvexa.app.ui.tools.time.DateCalculatorScreen
import com.nuvexa.app.ui.tools.time.StopwatchScreen
import com.nuvexa.app.ui.tools.time.TimerScreen
import com.nuvexa.app.ui.tools.time.UnixTimestampScreen
import com.nuvexa.app.ui.tools.time.WorkingDaysCalculatorScreen
import com.nuvexa.app.ui.tools.time.WorldClockScreen

/** Dispatches a registered tool id to its working screen and shared chrome. */
@Composable
fun ToolScreenHost(tool: Tool, onBack: () -> Unit) {
    val chromeViewModel: ToolChromeViewModel = hiltViewModel()

    // bind() records recent usage and starts a Flow collector, so it belongs in an effect rather
    // than the composition body. This prevents recomposition from being treated as user activity.
    LaunchedEffect(tool.id) {
        chromeViewModel.bind(tool.id)
    }

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
            "calc_scientific" -> ScientificCalculatorScreen(modifier, recordHistory)
            "calc_percentage" -> PercentageCalculatorScreen(modifier, recordHistory)
            "calc_discount" -> DiscountCalculatorScreen(modifier, recordHistory)
            "calc_tip" -> TipCalculatorScreen(modifier, recordHistory)
            "calc_split_bill" -> SplitBillScreen(modifier, recordHistory)
            "calc_age" -> AgeCalculatorScreen(modifier, recordHistory)
            "calc_date_diff" -> DateDifferenceScreen(modifier, recordHistory)
            "calc_simple_interest" -> SimpleInterestScreen(modifier, recordHistory)
            "calc_compound_interest" -> CompoundInterestScreen(modifier, recordHistory)
            "calc_emi" -> EmiCalculatorScreen(modifier, recordHistory)
            "calc_fraction" -> FractionCalculatorScreen(modifier, recordHistory)
            "calc_ratio" -> RatioCalculatorScreen(modifier, recordHistory)
            "calc_average" -> AverageCalculatorScreen(modifier, recordHistory)
            "calc_markup" -> MarkupCalculatorScreen(modifier, recordHistory)
            "calc_profit_margin" -> ProfitMarginCalculatorScreen(modifier, recordHistory)
            "calc_mortgage" -> MortgageCalculatorScreen(modifier, recordHistory)
            "calc_savings" -> SavingsCalculatorScreen(modifier, recordHistory)
            "calc_work_hours" -> WorkHoursCalculatorScreen(modifier, recordHistory)
            "calc_trip_cost" -> FuelTripCostScreen(modifier, recordHistory)

            "unit_converter" -> UnitConverterScreen(modifier)
            "currency_calculator" -> CurrencyCalculatorScreen(modifier)

            "text_analyzer" -> TextAnalyzerScreen(modifier)
            "text_case_converter" -> CaseConverterScreen(modifier)
            "text_cleaner" -> TextCleanerScreen(modifier)
            "text_base64" -> Base64ToolScreen(modifier)
            "text_url_encode" -> UrlEncoderScreen(modifier)
            "text_json_formatter" -> JsonFormatterScreen(modifier)
            "text_extract_numbers" -> ExtractNumbersScreen(modifier)
            "text_extract_emails" -> ExtractEmailsScreen(modifier)
            "text_extract_urls" -> ExtractUrlsScreen(modifier)

            "sec_password_generator" -> PasswordGeneratorScreen(modifier)
            "sec_password_audit" -> PasswordStrengthScreen(modifier)
            "sec_file_checksum" -> FileChecksumScreen(modifier, recordHistory)
            "sec_pin_generator" -> PinGeneratorScreen(modifier)
            "sec_uuid_generator" -> UuidGeneratorScreen(modifier)
            "sec_random_number" -> RandomNumberScreen(modifier)
            "sec_hash_generator" -> HashGeneratorScreen(modifier)
            "sec_passphrase_generator" -> PassphraseGeneratorScreen(modifier)
            "sec_hmac_generator" -> HmacGeneratorScreen(modifier)
            "sec_text_encryption" -> TextEncryptionScreen(modifier)

            "qr_generator" -> QrGeneratorScreen(modifier, recordHistory)
            "qr_scanner" -> QrScannerScreen(modifier, recordHistory)
            "color_converter" -> ColorConverterScreen(modifier)
            "color_palette_generator" -> PaletteGeneratorScreen(modifier)

            "time_world_clock" -> WorldClockScreen(modifier)
            "time_stopwatch" -> StopwatchScreen(modifier)
            "time_timer" -> TimerScreen(modifier)
            "time_unix_converter" -> UnixTimestampScreen(modifier)
            "time_date_calculator" -> DateCalculatorScreen(modifier)
            "time_working_days" -> WorkingDaysCalculatorScreen(modifier)

            "dev_regex_tester" -> RegexTesterScreen(modifier)
            "dev_xml_formatter" -> XmlFormatterScreen(modifier)
            "dev_html_formatter" -> HtmlFormatterScreen(modifier)
            "dev_css_formatter" -> CssFormatterScreen(modifier)
            "dev_jwt_decoder" -> JwtDecoderScreen(modifier)

            "device_info" -> DeviceInfoScreen(modifier)
            "image_compressor" -> ImageCompressorScreen(modifier, recordHistory)
            "image_resizer" -> ImageResizerScreen(modifier, recordHistory)

            "math_prime_checker" -> PrimeCheckerScreen(modifier)
            "math_prime_generator" -> PrimeGeneratorScreen(modifier)
            "math_gcd_lcm" -> GcdLcmScreen(modifier)
            "math_factorial" -> FactorialScreen(modifier)
            "math_fibonacci" -> FibonacciScreen(modifier)
            "math_binary_converter" -> BinaryConverterScreen(modifier)
            "math_statistics" -> StatisticsScreen(modifier)

            "net_subnet_calculator" -> SubnetCalculatorScreen(modifier)

            "pdf_images_to_pdf" -> ImagesToPdfScreen(modifier, recordHistory)
            "pdf_text_to_pdf" -> TextToPdfScreen(modifier, recordHistory)
            "pdf_to_images" -> PdfToImagesScreen(modifier, recordHistory)
            "pdf_viewer" -> PdfViewerScreen(modifier)
            "pdf_inspector" -> PdfInspectorScreen(modifier, recordHistory)
            "pdf_merge" -> PdfMergeScreen(modifier, recordHistory)
            "pdf_split" -> PdfSplitScreen(modifier, recordHistory)
            "pdf_rotate" -> PdfRotateScreen(modifier, recordHistory)
            "pdf_watermark" -> PdfWatermarkScreen(modifier, recordHistory)
            "pdf_organize" -> PdfOrganizeScreen(modifier, recordHistory)

            "ocr_from_image" -> OcrFromImageScreen(modifier, recordHistory)
            "ocr_from_camera" -> OcrFromCameraScreen(modifier, recordHistory)
            "ocr_from_pdf" -> OcrFromPdfScreen(modifier, recordHistory)
        }
    }
}
