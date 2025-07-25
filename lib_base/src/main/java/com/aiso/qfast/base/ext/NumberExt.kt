@file:Suppress("KotlinConstantConditions")

package com.aiso.qfast.base.ext

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.TypedValue

/**
 * dp 转换成 px
 * */
fun Number.dp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), Resources.getSystem().displayMetrics
    )
}

/**
 * dp 转换成 px
 * */
fun Int.dp(): Int {
    val value = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), Resources.getSystem().displayMetrics
    )
    return when {
        value > 0 -> (value + 0.5f).toInt()
        value < 0 -> (value - 0.5f).toInt()
        else -> value.toInt()
    }
}

/**
 * sp 转换成 px
 * */
fun Number.sp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        toFloat(), Resources.getSystem().displayMetrics
    )
}

/**
 * sp 转换成 px
 * */
fun Int.sp(): Int {
    val value = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        toFloat(), Resources.getSystem().displayMetrics
    )
    return when {
        value > 0 -> (value + 0.5f).toInt()
        value < 0 -> (value - 0.5f).toInt()
        else -> value.toInt()
    }
}

/**
 * 将Int按对应的格式转成String
 */
@SuppressLint("DefaultLocale")
fun Int.formatCount(): String {
    return when {
        this < 1000 -> this.toString()
        this < 10_000 -> String.format("%.1fK", this / 1000.0)
        else -> String.format("%.1fW", this / 10000.0)
    }
}

