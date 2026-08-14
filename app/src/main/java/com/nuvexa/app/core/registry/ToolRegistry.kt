package com.nuvexa.app.core.registry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import com.nuvexa.app.R
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.model.ToolCategory

/**
 * The single source of truth for every tool available in the app. Adding a tool means
 * adding an entry here (visible everywhere: search, categories, favorites) and a matching
 * screen in `ui.tools` wired into `ToolScreenHost`. Nothing is listed here unless it has a
 * real, working screen — no placeholders, no "coming soon" entries.
 */
object ToolRegistry {

    val tools: List<Tool> = listOf(
        Tool("calc_basic", ToolCategory.CALCULATORS, R.string.tool_calc_basic_name, R.string.tool_calc_basic_desc, R.string.tool_calc_basic_keywords, Icons.Filled.Calculate),
        Tool("calc_percentage", ToolCategory.CALCULATORS, R.string.tool_calc_percentage_name, R.string.tool_calc_percentage_desc, R.string.tool_calc_percentage_keywords, Icons.Filled.Percent),
        Tool("calc_discount", ToolCategory.CALCULATORS, R.string.tool_calc_discount_name, R.string.tool_calc_discount_desc, R.string.tool_calc_discount_keywords, Icons.Filled.LocalOffer),
        Tool("calc_tip", ToolCategory.CALCULATORS, R.string.tool_calc_tip_name, R.string.tool_calc_tip_desc, R.string.tool_calc_tip_keywords, Icons.Filled.AttachMoney),
        Tool("calc_split_bill", ToolCategory.CALCULATORS, R.string.tool_calc_split_bill_name, R.string.tool_calc_split_bill_desc, R.string.tool_calc_split_bill_keywords, Icons.Filled.Groups),
        Tool("calc_age", ToolCategory.CALCULATORS, R.string.tool_calc_age_name, R.string.tool_calc_age_desc, R.string.tool_calc_age_keywords, Icons.Filled.Cake),
        Tool("calc_date_diff", ToolCategory.CALCULATORS, R.string.tool_calc_date_diff_name, R.string.tool_calc_date_diff_desc, R.string.tool_calc_date_diff_keywords, Icons.Filled.DateRange),
        Tool("calc_simple_interest", ToolCategory.CALCULATORS, R.string.tool_calc_simple_interest_name, R.string.tool_calc_simple_interest_desc, R.string.tool_calc_simple_interest_keywords, Icons.Filled.TrendingUp),
        Tool("calc_compound_interest", ToolCategory.CALCULATORS, R.string.tool_calc_compound_interest_name, R.string.tool_calc_compound_interest_desc, R.string.tool_calc_compound_interest_keywords, Icons.Filled.ShowChart),
        Tool("calc_emi", ToolCategory.CALCULATORS, R.string.tool_calc_emi_name, R.string.tool_calc_emi_desc, R.string.tool_calc_emi_keywords, Icons.Filled.AccountBalance),

        Tool("unit_converter", ToolCategory.CONVERTER, R.string.tool_unit_converter_name, R.string.tool_unit_converter_desc, R.string.tool_unit_converter_keywords, Icons.Filled.SwapHoriz),

        Tool("currency_calculator", ToolCategory.CURRENCY, R.string.tool_currency_calculator_name, R.string.tool_currency_calculator_desc, R.string.tool_currency_calculator_keywords, Icons.Filled.AttachMoney),

        Tool("text_analyzer", ToolCategory.TEXT, R.string.tool_text_analyzer_name, R.string.tool_text_analyzer_desc, R.string.tool_text_analyzer_keywords, Icons.Filled.TextFields),
        Tool("text_case_converter", ToolCategory.TEXT, R.string.tool_text_case_converter_name, R.string.tool_text_case_converter_desc, R.string.tool_text_case_converter_keywords, Icons.Filled.SortByAlpha),
        Tool("text_cleaner", ToolCategory.TEXT, R.string.tool_text_cleaner_name, R.string.tool_text_cleaner_desc, R.string.tool_text_cleaner_keywords, Icons.Filled.CleaningServices),
        Tool("text_base64", ToolCategory.TEXT, R.string.tool_text_base64_name, R.string.tool_text_base64_desc, R.string.tool_text_base64_keywords, Icons.Filled.Code),
        Tool("text_url_encode", ToolCategory.TEXT, R.string.tool_text_url_encode_name, R.string.tool_text_url_encode_desc, R.string.tool_text_url_encode_keywords, Icons.Filled.Link),
        Tool("text_json_formatter", ToolCategory.TEXT, R.string.tool_text_json_formatter_name, R.string.tool_text_json_formatter_desc, R.string.tool_text_json_formatter_keywords, Icons.Filled.Description),

        Tool("sec_password_generator", ToolCategory.SECURITY, R.string.tool_sec_password_generator_name, R.string.tool_sec_password_generator_desc, R.string.tool_sec_password_generator_keywords, Icons.Filled.Lock),
        Tool("sec_pin_generator", ToolCategory.SECURITY, R.string.tool_sec_pin_generator_name, R.string.tool_sec_pin_generator_desc, R.string.tool_sec_pin_generator_keywords, Icons.Filled.Dialpad),
        Tool("sec_uuid_generator", ToolCategory.SECURITY, R.string.tool_sec_uuid_generator_name, R.string.tool_sec_uuid_generator_desc, R.string.tool_sec_uuid_generator_keywords, Icons.Filled.Fingerprint),
        Tool("sec_random_number", ToolCategory.SECURITY, R.string.tool_sec_random_number_name, R.string.tool_sec_random_number_desc, R.string.tool_sec_random_number_keywords, Icons.Filled.Casino),
        Tool("sec_hash_generator", ToolCategory.SECURITY, R.string.tool_sec_hash_generator_name, R.string.tool_sec_hash_generator_desc, R.string.tool_sec_hash_generator_keywords, Icons.Filled.Tag),

        Tool("qr_generator", ToolCategory.QR, R.string.tool_qr_generator_name, R.string.tool_qr_generator_desc, R.string.tool_qr_generator_keywords, Icons.Filled.QrCode),
        Tool("qr_scanner", ToolCategory.QR, R.string.tool_qr_scanner_name, R.string.tool_qr_scanner_desc, R.string.tool_qr_scanner_keywords, Icons.Filled.QrCodeScanner),

        Tool("color_converter", ToolCategory.COLOR, R.string.tool_color_converter_name, R.string.tool_color_converter_desc, R.string.tool_color_converter_keywords, Icons.Filled.Colorize),
        Tool("color_palette_generator", ToolCategory.COLOR, R.string.tool_color_palette_generator_name, R.string.tool_color_palette_generator_desc, R.string.tool_color_palette_generator_keywords, Icons.Filled.Palette),

        Tool("time_world_clock", ToolCategory.TIME, R.string.tool_time_world_clock_name, R.string.tool_time_world_clock_desc, R.string.tool_time_world_clock_keywords, Icons.Filled.Public),
        Tool("time_stopwatch", ToolCategory.TIME, R.string.tool_time_stopwatch_name, R.string.tool_time_stopwatch_desc, R.string.tool_time_stopwatch_keywords, Icons.Filled.Timer),
        Tool("time_timer", ToolCategory.TIME, R.string.tool_time_timer_name, R.string.tool_time_timer_desc, R.string.tool_time_timer_keywords, Icons.Filled.HourglassBottom),
        Tool("time_unix_converter", ToolCategory.TIME, R.string.tool_time_unix_converter_name, R.string.tool_time_unix_converter_desc, R.string.tool_time_unix_converter_keywords, Icons.Filled.AccessTime),
        Tool("time_date_calculator", ToolCategory.TIME, R.string.tool_time_date_calculator_name, R.string.tool_time_date_calculator_desc, R.string.tool_time_date_calculator_keywords, Icons.Filled.Event),

        Tool("dev_regex_tester", ToolCategory.DEVELOPER, R.string.tool_dev_regex_tester_name, R.string.tool_dev_regex_tester_desc, R.string.tool_dev_regex_tester_keywords, Icons.Filled.FindReplace),
        Tool("dev_lorem_ipsum", ToolCategory.DEVELOPER, R.string.tool_dev_lorem_ipsum_name, R.string.tool_dev_lorem_ipsum_desc, R.string.tool_dev_lorem_ipsum_keywords, Icons.Filled.Article),

        Tool("device_info", ToolCategory.DEVICE, R.string.tool_device_info_name, R.string.tool_device_info_desc, R.string.tool_device_info_keywords, Icons.Filled.PhoneAndroid),

        Tool("image_compressor", ToolCategory.IMAGE, R.string.tool_image_compressor_name, R.string.tool_image_compressor_desc, R.string.tool_image_compressor_keywords, Icons.Filled.Image),
        Tool("image_resizer", ToolCategory.IMAGE, R.string.tool_image_resizer_name, R.string.tool_image_resizer_desc, R.string.tool_image_resizer_keywords, Icons.Filled.AspectRatio),
    )

    private val byId: Map<String, Tool> = tools.associateBy { it.id }

    fun findById(id: String): Tool? = byId[id]

    fun byCategory(category: ToolCategory): List<Tool> = tools.filter { it.category == category }

    val quickActionIds: List<String> = listOf(
        "qr_scanner", "image_compressor", "calc_basic", "unit_converter", "sec_password_generator", "text_analyzer",
    )
}
