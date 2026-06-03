package com.pedroaba.tccmobile.ui.format

import kotlin.math.round

fun parseHeightCmToMeters(value: String): Double? {
    val parsed = value.replace(",", ".").trim().ifBlank { return null }.toDoubleOrNull()
        ?: return null
    return (parsed / 100.0).takeIf { it > 0.0 }
}

fun formatHeightMetersToCm(value: Double): String {
    val centimeters = round(value * 100.0 * 10.0) / 10.0
    return if (centimeters % 1.0 == 0.0) {
        centimeters.toInt().toString()
    } else {
        centimeters.toString()
    }
}
