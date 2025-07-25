package com.aiso.qfast.base.view


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.NumberPicker

@SuppressLint("NewApi")
class SelectorNumberPicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NumberPicker(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
    }

    private val cornerRadius = 12.dp(context)

    override fun onDraw(canvas: Canvas) {
        val itemHeight = height / (displayedValues?.size ?: (maxValue - minValue + 1))
        val centerY = height / 2
        val top = centerY - itemHeight / 2
        val bottom = centerY + itemHeight / 2

        val rect = RectF(0f, top.toFloat(), width.toFloat(), bottom.toFloat())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        super.onDraw(canvas)
    }

    private fun Int.dp(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }

    private fun Float.dp(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }
}
