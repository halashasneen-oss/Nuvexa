package com.nuvexa.app.core.model

import com.nuvexa.app.R

data class UnitDefinition(val symbol: String, val toBase: (Double) -> Double, val fromBase: (Double) -> Double)

private fun linearUnits(vararg pairs: Pair<String, Double>): List<UnitDefinition> =
    pairs.map { (symbol, factor) -> UnitDefinition(symbol, { value -> value * factor }, { base -> base / factor }) }

enum class UnitCategory(val labelRes: Int, val units: List<UnitDefinition>) {
    LENGTH(
        R.string.unit_type_length,
        linearUnits(
            "mm" to 0.001, "cm" to 0.01, "m" to 1.0, "km" to 1000.0,
            "in" to 0.0254, "ft" to 0.3048, "yd" to 0.9144, "mi" to 1609.344,
        ),
    ),
    WEIGHT(
        R.string.unit_type_weight,
        linearUnits("mg" to 0.001, "g" to 1.0, "kg" to 1000.0, "oz" to 28.3495, "lb" to 453.592, "t" to 1_000_000.0),
    ),
    TEMPERATURE(
        R.string.unit_type_temperature,
        listOf(
            UnitDefinition("°C", { it }, { it }),
            UnitDefinition("°F", { (it - 32) * 5.0 / 9.0 }, { it * 9.0 / 5.0 + 32 }),
            UnitDefinition("K", { it - 273.15 }, { it + 273.15 }),
        ),
    ),
    AREA(
        R.string.unit_type_area,
        linearUnits(
            "m²" to 1.0, "km²" to 1_000_000.0, "ft²" to 0.092903, "yd²" to 0.836127,
            "acre" to 4046.86, "ha" to 10000.0,
        ),
    ),
    VOLUME(
        R.string.unit_type_volume,
        linearUnits(
            "ml" to 0.001, "l" to 1.0, "gal" to 3.78541, "qt" to 0.946353,
            "pt" to 0.473176, "cup" to 0.24, "fl oz" to 0.0295735, "m³" to 1000.0,
        ),
    ),
    SPEED(
        R.string.unit_type_speed,
        linearUnits("m/s" to 1.0, "km/h" to 0.277778, "mph" to 0.44704, "knot" to 0.514444, "ft/s" to 0.3048),
    ),
    TIME(
        R.string.unit_type_time,
        linearUnits("s" to 1.0, "min" to 60.0, "h" to 3600.0, "day" to 86400.0, "week" to 604800.0),
    ),
    DATA(
        R.string.unit_type_data,
        linearUnits(
            "bit" to 0.125, "B" to 1.0, "KB" to 1024.0, "MB" to 1024.0 * 1024,
            "GB" to 1024.0 * 1024 * 1024, "TB" to 1024.0 * 1024 * 1024 * 1024,
        ),
    ),
}
