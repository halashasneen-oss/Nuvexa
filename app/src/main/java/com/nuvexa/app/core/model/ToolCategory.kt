package com.nuvexa.app.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.nuvexa.app.R

enum class ToolCategory(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
) {
    CALCULATORS("calculators", R.string.category_calculators, R.string.category_calculators_desc, Icons.Filled.Calculate),
    CONVERTER("converter", R.string.category_converter, R.string.category_converter_desc, Icons.Filled.SwapHoriz),
    CURRENCY("currency", R.string.category_currency, R.string.category_currency_desc, Icons.Filled.Calculate),
    TEXT("text", R.string.category_text, R.string.category_text_desc, Icons.Filled.TextFields),
    SECURITY("security", R.string.category_security, R.string.category_security_desc, Icons.Filled.Security),
    QR("qr", R.string.category_qr, R.string.category_qr_desc, Icons.Filled.QrCode),
    COLOR("color", R.string.category_color, R.string.category_color_desc, Icons.Filled.Palette),
    TIME("time", R.string.category_time, R.string.category_time_desc, Icons.Filled.Schedule),
    DEVELOPER("developer", R.string.category_developer, R.string.category_developer_desc, Icons.Filled.Code),
    DEVICE("device", R.string.category_device, R.string.category_device_desc, Icons.Filled.PhoneAndroid),
    IMAGE("image", R.string.category_image, R.string.category_image_desc, Icons.Filled.Image),
    MATH("math", R.string.category_math, R.string.category_math_desc, Icons.Filled.Functions),
    NETWORK("network", R.string.category_network, R.string.category_network_desc, Icons.Filled.Wifi),
    ;

    companion object {
        val fallbackIcon: ImageVector = Icons.Filled.Category
    }
}
