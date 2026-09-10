package com.nuvexa.app.core.registry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import com.nuvexa.app.R
import com.nuvexa.app.core.model.Tool
import com.nuvexa.app.core.model.ToolCategory

/**
 * Single source of truth for every visible tool. Reference-only or duplicated utilities are
 * intentionally kept out of the registry so browsing stays focused on tools that perform a
 * useful action. Every id here has a matching branch in ToolScreenHost.
 */
object ToolRegistry {

    val tools: List<Tool> = listOf(
        // Calculators
        Tool("calc_basic", ToolCategory.CALCULATORS, R.string.tool_calc_basic_name, R.string.tool_calc_basic_desc, R.string.tool_calc_basic_keywords, Icons.Rounded.Calculate),
        Tool("calc_scientific", ToolCategory.CALCULATORS, R.string.tool_calc_scientific_name, R.string.tool_calc_scientific_desc, R.string.tool_calc_scientific_keywords, Icons.Rounded.Functions),
        Tool("calc_percentage", ToolCategory.CALCULATORS, R.string.tool_calc_percentage_name, R.string.tool_calc_percentage_desc, R.string.tool_calc_percentage_keywords, Icons.Rounded.Percent),
        Tool("calc_discount", ToolCategory.CALCULATORS, R.string.tool_calc_discount_name, R.string.tool_calc_discount_desc, R.string.tool_calc_discount_keywords, Icons.Rounded.LocalOffer),
        Tool("calc_tip", ToolCategory.CALCULATORS, R.string.tool_calc_tip_name, R.string.tool_calc_tip_desc, R.string.tool_calc_tip_keywords, Icons.Rounded.AttachMoney),
        Tool("calc_split_bill", ToolCategory.CALCULATORS, R.string.tool_calc_split_bill_name, R.string.tool_calc_split_bill_desc, R.string.tool_calc_split_bill_keywords, Icons.Rounded.Groups),
        Tool("calc_age", ToolCategory.CALCULATORS, R.string.tool_calc_age_name, R.string.tool_calc_age_desc, R.string.tool_calc_age_keywords, Icons.Rounded.Cake),
        Tool("calc_date_diff", ToolCategory.CALCULATORS, R.string.tool_calc_date_diff_name, R.string.tool_calc_date_diff_desc, R.string.tool_calc_date_diff_keywords, Icons.Rounded.DateRange),
        Tool("calc_simple_interest", ToolCategory.CALCULATORS, R.string.tool_calc_simple_interest_name, R.string.tool_calc_simple_interest_desc, R.string.tool_calc_simple_interest_keywords, Icons.Rounded.TrendingUp),
        Tool("calc_compound_interest", ToolCategory.CALCULATORS, R.string.tool_calc_compound_interest_name, R.string.tool_calc_compound_interest_desc, R.string.tool_calc_compound_interest_keywords, Icons.Rounded.ShowChart),
        Tool("calc_emi", ToolCategory.CALCULATORS, R.string.tool_calc_emi_name, R.string.tool_calc_emi_desc, R.string.tool_calc_emi_keywords, Icons.Rounded.AccountBalance),
        Tool("calc_fraction", ToolCategory.CALCULATORS, R.string.tool_calc_fraction_name, R.string.tool_calc_fraction_desc, R.string.tool_calc_fraction_keywords, Icons.Rounded.Functions),
        Tool("calc_ratio", ToolCategory.CALCULATORS, R.string.tool_calc_ratio_name, R.string.tool_calc_ratio_desc, R.string.tool_calc_ratio_keywords, Icons.Rounded.Calculate),
        Tool("calc_average", ToolCategory.CALCULATORS, R.string.tool_calc_average_name, R.string.tool_calc_average_desc, R.string.tool_calc_average_keywords, Icons.Rounded.ShowChart),
        Tool("calc_markup", ToolCategory.CALCULATORS, R.string.tool_calc_markup_name, R.string.tool_calc_markup_desc, R.string.tool_calc_markup_keywords, Icons.Rounded.LocalOffer),
        Tool("calc_profit_margin", ToolCategory.CALCULATORS, R.string.tool_calc_profit_margin_name, R.string.tool_calc_profit_margin_desc, R.string.tool_calc_profit_margin_keywords, Icons.Rounded.TrendingUp),
        Tool("calc_mortgage", ToolCategory.CALCULATORS, R.string.tool_calc_mortgage_name, R.string.tool_calc_mortgage_desc, R.string.tool_calc_mortgage_keywords, Icons.Rounded.AccountBalance),
        Tool("calc_savings", ToolCategory.CALCULATORS, R.string.tool_calc_savings_name, R.string.tool_calc_savings_desc, R.string.tool_calc_savings_keywords, Icons.Rounded.Savings),
        Tool("calc_work_hours", ToolCategory.CALCULATORS, R.string.tool_calc_work_hours_name, R.string.tool_calc_work_hours_desc, R.string.tool_calc_work_hours_keywords, Icons.Rounded.Schedule),
        Tool("calc_trip_cost", ToolCategory.CALCULATORS, R.string.tool_calc_trip_cost_name, R.string.tool_calc_trip_cost_desc, R.string.tool_calc_trip_cost_keywords, Icons.Rounded.LocalGasStation),

        // Conversion and currency
        Tool("unit_converter", ToolCategory.CONVERTER, R.string.tool_unit_converter_name, R.string.tool_unit_converter_desc, R.string.tool_unit_converter_keywords, Icons.Rounded.SwapHoriz),
        Tool("currency_calculator", ToolCategory.CURRENCY, R.string.tool_currency_calculator_name, R.string.tool_currency_calculator_desc, R.string.tool_currency_calculator_keywords, Icons.Rounded.AttachMoney),

        // Text
        Tool("text_analyzer", ToolCategory.TEXT, R.string.tool_text_analyzer_name, R.string.tool_text_analyzer_desc, R.string.tool_text_analyzer_keywords, Icons.Rounded.TextFields),
        Tool("text_case_converter", ToolCategory.TEXT, R.string.tool_text_case_converter_name, R.string.tool_text_case_converter_desc, R.string.tool_text_case_converter_keywords, Icons.Rounded.SortByAlpha),
        Tool("text_cleaner", ToolCategory.TEXT, R.string.tool_text_cleaner_name, R.string.tool_text_cleaner_desc, R.string.tool_text_cleaner_keywords, Icons.Rounded.CleaningServices),
        Tool("text_base64", ToolCategory.TEXT, R.string.tool_text_base64_name, R.string.tool_text_base64_desc, R.string.tool_text_base64_keywords, Icons.Rounded.Code),
        Tool("text_url_encode", ToolCategory.TEXT, R.string.tool_text_url_encode_name, R.string.tool_text_url_encode_desc, R.string.tool_text_url_encode_keywords, Icons.Rounded.Link),
        Tool("text_json_formatter", ToolCategory.TEXT, R.string.tool_text_json_formatter_name, R.string.tool_text_json_formatter_desc, R.string.tool_text_json_formatter_keywords, Icons.Rounded.Description),
        Tool("text_extract_numbers", ToolCategory.TEXT, R.string.tool_text_extract_numbers_name, R.string.tool_text_extract_numbers_desc, R.string.tool_text_extract_numbers_keywords, Icons.Rounded.FormatListNumbered),
        Tool("text_extract_emails", ToolCategory.TEXT, R.string.tool_text_extract_emails_name, R.string.tool_text_extract_emails_desc, R.string.tool_text_extract_emails_keywords, Icons.Rounded.Email),
        Tool("text_extract_urls", ToolCategory.TEXT, R.string.tool_text_extract_urls_name, R.string.tool_text_extract_urls_desc, R.string.tool_text_extract_urls_keywords, Icons.Rounded.Link),

        // Privacy and security
        Tool("sec_password_generator", ToolCategory.SECURITY, R.string.tool_sec_password_generator_name, R.string.tool_sec_password_generator_desc, R.string.tool_sec_password_generator_keywords, Icons.Rounded.Lock),
        Tool("sec_password_audit", ToolCategory.SECURITY, R.string.tool_sec_password_audit_name, R.string.tool_sec_password_audit_desc, R.string.tool_sec_password_audit_keywords, Icons.Rounded.Security),
        Tool("sec_file_checksum", ToolCategory.SECURITY, R.string.tool_sec_file_checksum_name, R.string.tool_sec_file_checksum_desc, R.string.tool_sec_file_checksum_keywords, Icons.Rounded.InsertDriveFile),
        Tool("sec_pin_generator", ToolCategory.SECURITY, R.string.tool_sec_pin_generator_name, R.string.tool_sec_pin_generator_desc, R.string.tool_sec_pin_generator_keywords, Icons.Rounded.Dialpad),
        Tool("sec_uuid_generator", ToolCategory.SECURITY, R.string.tool_sec_uuid_generator_name, R.string.tool_sec_uuid_generator_desc, R.string.tool_sec_uuid_generator_keywords, Icons.Rounded.Fingerprint),
        Tool("sec_random_number", ToolCategory.SECURITY, R.string.tool_sec_random_number_name, R.string.tool_sec_random_number_desc, R.string.tool_sec_random_number_keywords, Icons.Rounded.Casino),
        Tool("sec_hash_generator", ToolCategory.SECURITY, R.string.tool_sec_hash_generator_name, R.string.tool_sec_hash_generator_desc, R.string.tool_sec_hash_generator_keywords, Icons.Rounded.Tag),
        Tool("sec_passphrase_generator", ToolCategory.SECURITY, R.string.tool_sec_passphrase_generator_name, R.string.tool_sec_passphrase_generator_desc, R.string.tool_sec_passphrase_generator_keywords, Icons.Rounded.Key),
        Tool("sec_hmac_generator", ToolCategory.SECURITY, R.string.tool_sec_hmac_generator_name, R.string.tool_sec_hmac_generator_desc, R.string.tool_sec_hmac_generator_keywords, Icons.Rounded.Tag),
        Tool("sec_text_encryption", ToolCategory.SECURITY, R.string.tool_sec_text_encryption_name, R.string.tool_sec_text_encryption_desc, R.string.tool_sec_text_encryption_keywords, Icons.Rounded.Lock),

        // QR, colors and time
        Tool("qr_generator", ToolCategory.QR, R.string.tool_qr_generator_name, R.string.tool_qr_generator_desc, R.string.tool_qr_generator_keywords, Icons.Rounded.QrCode),
        Tool("qr_scanner", ToolCategory.QR, R.string.tool_qr_scanner_name, R.string.tool_qr_scanner_desc, R.string.tool_qr_scanner_keywords, Icons.Rounded.QrCodeScanner),
        Tool("color_converter", ToolCategory.COLOR, R.string.tool_color_converter_name, R.string.tool_color_converter_desc, R.string.tool_color_converter_keywords, Icons.Rounded.Colorize),
        Tool("color_palette_generator", ToolCategory.COLOR, R.string.tool_color_palette_generator_name, R.string.tool_color_palette_generator_desc, R.string.tool_color_palette_generator_keywords, Icons.Rounded.Palette),
        Tool("time_world_clock", ToolCategory.TIME, R.string.tool_time_world_clock_name, R.string.tool_time_world_clock_desc, R.string.tool_time_world_clock_keywords, Icons.Rounded.Public),
        Tool("time_stopwatch", ToolCategory.TIME, R.string.tool_time_stopwatch_name, R.string.tool_time_stopwatch_desc, R.string.tool_time_stopwatch_keywords, Icons.Rounded.Timer),
        Tool("time_timer", ToolCategory.TIME, R.string.tool_time_timer_name, R.string.tool_time_timer_desc, R.string.tool_time_timer_keywords, Icons.Rounded.HourglassBottom),
        Tool("time_unix_converter", ToolCategory.TIME, R.string.tool_time_unix_converter_name, R.string.tool_time_unix_converter_desc, R.string.tool_time_unix_converter_keywords, Icons.Rounded.AccessTime),
        Tool("time_date_calculator", ToolCategory.TIME, R.string.tool_time_date_calculator_name, R.string.tool_time_date_calculator_desc, R.string.tool_time_date_calculator_keywords, Icons.Rounded.Event),
        Tool("time_working_days", ToolCategory.TIME, R.string.tool_time_working_days_name, R.string.tool_time_working_days_desc, R.string.tool_time_working_days_keywords, Icons.Rounded.Event),

        // Developer, device, image and math
        Tool("dev_regex_tester", ToolCategory.DEVELOPER, R.string.tool_dev_regex_tester_name, R.string.tool_dev_regex_tester_desc, R.string.tool_dev_regex_tester_keywords, Icons.Rounded.FindReplace),
        Tool("dev_xml_formatter", ToolCategory.DEVELOPER, R.string.tool_dev_xml_formatter_name, R.string.tool_dev_xml_formatter_desc, R.string.tool_dev_xml_formatter_keywords, Icons.Rounded.Code),
        Tool("dev_html_formatter", ToolCategory.DEVELOPER, R.string.tool_dev_html_formatter_name, R.string.tool_dev_html_formatter_desc, R.string.tool_dev_html_formatter_keywords, Icons.Rounded.Code),
        Tool("dev_css_formatter", ToolCategory.DEVELOPER, R.string.tool_dev_css_formatter_name, R.string.tool_dev_css_formatter_desc, R.string.tool_dev_css_formatter_keywords, Icons.Rounded.Code),
        Tool("dev_jwt_decoder", ToolCategory.DEVELOPER, R.string.tool_dev_jwt_decoder_name, R.string.tool_dev_jwt_decoder_desc, R.string.tool_dev_jwt_decoder_keywords, Icons.Rounded.VpnKey),
        Tool("device_info", ToolCategory.DEVICE, R.string.tool_device_info_name, R.string.tool_device_info_desc, R.string.tool_device_info_keywords, Icons.Rounded.PhoneAndroid),
        Tool("image_compressor", ToolCategory.IMAGE, R.string.tool_image_compressor_name, R.string.tool_image_compressor_desc, R.string.tool_image_compressor_keywords, Icons.Rounded.Image),
        Tool("image_resizer", ToolCategory.IMAGE, R.string.tool_image_resizer_name, R.string.tool_image_resizer_desc, R.string.tool_image_resizer_keywords, Icons.Rounded.AspectRatio),
        Tool("math_prime_checker", ToolCategory.MATH, R.string.tool_math_prime_checker_name, R.string.tool_math_prime_checker_desc, R.string.tool_math_prime_checker_keywords, Icons.Rounded.Search),
        Tool("math_prime_generator", ToolCategory.MATH, R.string.tool_math_prime_generator_name, R.string.tool_math_prime_generator_desc, R.string.tool_math_prime_generator_keywords, Icons.Rounded.FormatListNumbered),
        Tool("math_gcd_lcm", ToolCategory.MATH, R.string.tool_math_gcd_lcm_name, R.string.tool_math_gcd_lcm_desc, R.string.tool_math_gcd_lcm_keywords, Icons.Rounded.Functions),
        Tool("math_factorial", ToolCategory.MATH, R.string.tool_math_factorial_name, R.string.tool_math_factorial_desc, R.string.tool_math_factorial_keywords, Icons.Rounded.Tag),
        Tool("math_fibonacci", ToolCategory.MATH, R.string.tool_math_fibonacci_name, R.string.tool_math_fibonacci_desc, R.string.tool_math_fibonacci_keywords, Icons.Rounded.Timeline),
        Tool("math_binary_converter", ToolCategory.MATH, R.string.tool_math_binary_converter_name, R.string.tool_math_binary_converter_desc, R.string.tool_math_binary_converter_keywords, Icons.Rounded.Code),
        Tool("math_statistics", ToolCategory.MATH, R.string.tool_math_statistics_name, R.string.tool_math_statistics_desc, R.string.tool_math_statistics_keywords, Icons.Rounded.ShowChart),

        // Network — keep the real calculator, remove static reference-list tiles.
        Tool("net_subnet_calculator", ToolCategory.NETWORK, R.string.tool_net_subnet_calculator_name, R.string.tool_net_subnet_calculator_desc, R.string.tool_net_subnet_calculator_keywords, Icons.Rounded.Wifi),

        // PDF and OCR
        Tool("pdf_images_to_pdf", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_images_to_pdf_name, R.string.tool_pdf_images_to_pdf_desc, R.string.tool_pdf_images_to_pdf_keywords, Icons.Rounded.PictureAsPdf),
        Tool("pdf_text_to_pdf", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_text_to_pdf_name, R.string.tool_pdf_text_to_pdf_desc, R.string.tool_pdf_text_to_pdf_keywords, Icons.Rounded.Description),
        Tool("pdf_to_images", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_to_images_name, R.string.tool_pdf_to_images_desc, R.string.tool_pdf_to_images_keywords, Icons.Rounded.Image),
        Tool("pdf_viewer", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_viewer_name, R.string.tool_pdf_viewer_desc, R.string.tool_pdf_viewer_keywords, Icons.Rounded.PictureAsPdf),
        Tool("pdf_inspector", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_inspector_name, R.string.tool_pdf_inspector_desc, R.string.tool_pdf_inspector_keywords, Icons.Rounded.Search),
        Tool("pdf_merge", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_merge_name, R.string.tool_pdf_merge_desc, R.string.tool_pdf_merge_keywords, Icons.Rounded.PictureAsPdf),
        Tool("pdf_split", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_split_name, R.string.tool_pdf_split_desc, R.string.tool_pdf_split_keywords, Icons.Rounded.CallSplit),
        Tool("pdf_rotate", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_rotate_name, R.string.tool_pdf_rotate_desc, R.string.tool_pdf_rotate_keywords, Icons.Rounded.RotateRight),
        Tool("pdf_watermark", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_watermark_name, R.string.tool_pdf_watermark_desc, R.string.tool_pdf_watermark_keywords, Icons.Rounded.BrandingWatermark),
        Tool("pdf_organize", ToolCategory.PDF_DOCUMENT, R.string.tool_pdf_organize_name, R.string.tool_pdf_organize_desc, R.string.tool_pdf_organize_keywords, Icons.Rounded.Reorder),
        Tool("ocr_from_image", ToolCategory.OCR, R.string.tool_ocr_from_image_name, R.string.tool_ocr_from_image_desc, R.string.tool_ocr_from_image_keywords, Icons.Rounded.TextFields),
        Tool("ocr_from_camera", ToolCategory.OCR, R.string.tool_ocr_from_camera_name, R.string.tool_ocr_from_camera_desc, R.string.tool_ocr_from_camera_keywords, Icons.Rounded.CameraAlt),
        Tool("ocr_from_pdf", ToolCategory.OCR, R.string.tool_ocr_from_pdf_name, R.string.tool_ocr_from_pdf_desc, R.string.tool_ocr_from_pdf_keywords, Icons.Rounded.PictureAsPdf),
    )

    private val byId: Map<String, Tool> = tools.associateBy { it.id }

    fun findById(id: String): Tool? = byId[id]

    fun byCategory(category: ToolCategory): List<Tool> = tools.filter { it.category == category }

    val quickActionIds: List<String> = listOf(
        "calc_scientific",
        "sec_file_checksum",
        "qr_scanner",
        "pdf_inspector",
        "image_compressor",
        "calc_trip_cost",
    )
}
