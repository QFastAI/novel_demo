package com.aiso.qfast.base.ext

import android.annotation.SuppressLint
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

/**
 * 支持设置舍入模式的类型小数
 */
@SuppressLint("NewApi")
inline fun Any?.formatDecimalRoundingMode(
    decimalDigits: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    failValue: Double = 0.0
): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    numberFormat.roundingMode = roundingMode
    numberFormat.maximumFractionDigits = decimalDigits
    return try {
        numberFormat.format(this?.toString()?.toDouble())
            .formatDecimalInterruptOrFillMode(decimalDigits)
    } catch (e: NumberFormatException) {
        numberFormat.format(failValue).formatDecimalInterruptOrFillMode(decimalDigits)
    }
}

/**
 * 强模式小数格式化，超长的直接截取，长度不够填充0
 */
inline fun Any?.formatDecimalInterruptOrFillMode(
    decimalPlaces: Int = 2,
    failValue: Double = 0.0
): String = try {
        String.format("%.${decimalPlaces}f", this.toString().toDouble())
    } catch (e: NumberFormatException) {
        String.format("%.${decimalPlaces}f", failValue)
    }

/**
 * 强模式小数格式化，超长的直接截取，长度不够填充0
 */
inline fun Any?.formatDecimal(
    failValue: Double = 0.0
): Double =    try {
        toString().toDouble()
    } catch (e: NumberFormatException) {
        failValue
    }


inline fun <T> T?.ifNull(block: () -> T?): T? {
    return this ?: block()
}
