package com.nuvexa.app.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.nuvexa.app.R

enum class ToolCategory(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
) {
    CALCULATORS("calculators", R.string.category_calculators, R.string.category_calculators_desc, Icons.Rounded.Calculate),
    CONVERTER("converter", R.string.category_converter, R.string.category_converter_desc, Icons.Rounded.SwapHoriz),
    CURRENCY("currency", R.string.category_currency, R.string.category_currency_desc, Icons.Rounded.Calculate),
    TEXT("text", R.string.category_text, R.string.category_text_desc, Icons.Rounded.TextFields),
    SECURITY("security", R.string.category_security, R.string.category_security_desc, Icons.Rounded.Security),
    QR("qr", R.string.category_qr, R.string.category_qr_desc, Icons.Rounded.QrCode),
    COLOR("color", R.string.category_color, R.string.category_color_desc, Icons.Rounded.Palette),
    TIME("time", R.string.category_time, R.string.category_time_desc, Icons.Rounded.Schedule),
    DEVELOPER("developer", R.string.category_developer, R.string.category_developer_desc, Icons.Rounded.Code),
    DEVICE("device", R.string.category_device, R.string.category_device_desc, Icons.Rounded.PhoneAndroid),
    IMAGE("image", R.string.category_image, R.string.category_image_desc, Icons.Rounded.Image),
    MATH("math", R.string.category_math, R.string.category_math_desc, Icons.Rounded.Functions),
    NETWORK("network", R.string.category_network, R.string.category_network_desc, Icons.Rounded.Wifi),
    PDF_DOCUMENT("pdf", R.string.category_pdf, R.string.category_pdf_desc, Icons.Rounded.PictureAsPdf),
    OCR("ocr", R.string.category_ocr, R.string.category_ocr_desc, Icons.Rounded.DocumentScanner),
    ;

    companion object {
        val fallbackIcon: ImageVector = Icons.Rounded.Category
    }
}
