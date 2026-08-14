package com.nuvexa.app.core.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
    if (content.isEmpty()) return null
    return runCatching {
        val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M, EncodeHintType.MARGIN to 1)
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    }.getOrNull()
}

fun buildWifiQrPayload(ssid: String, password: String, isWpa: Boolean): String {
    val type = if (isWpa) "WPA" else "nopass"
    val escapedSsid = ssid.replace(";", "\\;").replace(",", "\\,")
    val escapedPassword = password.replace(";", "\\;").replace(",", "\\,")
    return if (isWpa) "WIFI:T:$type;S:$escapedSsid;P:$escapedPassword;;" else "WIFI:T:$type;S:$escapedSsid;;"
}
