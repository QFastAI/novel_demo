package com.aiso.qfast.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

object DensityUtils {

    /**
     * 将 dp 转换为 px
     */
    fun dpToPx(context: Context, dp: Float): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).toInt()
    }

    /**
     * 将 px 转换为 dp
     */
    fun pxToDp(context: Context, px: Float): Float {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return px / displayMetrics.density
    }

}

val Int.dp: Float get()= this.toFloat().dp
val Int.dpi: Int get() = this.toFloat().dpi

val Number.dp: Float get(){
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics)
}
val Number.dpi: Int get(){
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    return (this.toFloat() * displayMetrics.density).toInt()
}
