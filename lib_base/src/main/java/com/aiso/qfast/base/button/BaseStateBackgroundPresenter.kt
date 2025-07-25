package com.aiso.qfast.base.button

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.*
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.CallSuper
import kotlin.math.max

@SuppressLint("NewApi")
open class BaseStateBackgroundPresenter(private val targetView: View) {
    protected var backgroundColor: ColorStateList = ColorStateList.valueOf(Color.TRANSPARENT)
    protected var backgroundDrawable: Drawable? = null
    protected var backgroundPressedColor: ColorStateList = ColorStateList.valueOf(Color.TRANSPARENT)
    protected var cornerRadius: Float = 0f
    protected var strokeColor: ColorStateList? = null
    protected var strokeWidth: Float = 0f

    private var realCornerRadius: Float = 0f

    private val strokeCornerRadius: Float
        get() = if (cornerRadius <= 0) {
            0f
        } else {
            if (realCornerRadius > 0) {
                max(realCornerRadius - strokeWidth / 2f, 0f) + CORNER_RADIUS_MAGIC
            } else {
                max(cornerRadius - strokeWidth / 2f, 0f) + CORNER_RADIUS_MAGIC
            }
        }

    private var internalStrokeDrawable: GradientDrawable? = null
    private var internalRippleMaskDrawable: Drawable? = null

    init {
        targetView.clipToOutline = true
        targetView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                if (view.width > 0 && view.height > 0) {
                    realCornerRadius = if (cornerRadius > view.height / 2f) {
                        view.height / 2f
                    } else {
                        cornerRadius
                    }
                    outline.setRoundRect(0, 0, view.width, view.height, realCornerRadius)
                }
            }
        }
    }

    @CallSuper
    open fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed && Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            // MaterialButton 有这样的判断…… 可能是修复bug，Workaround for API 21 ripple bug (possibly internal in GradientDrawable)
            internalRippleMaskDrawable?.setBounds(0, 0, targetView.width, targetView.height)
        }
    }

    fun updateRadius(radius: Float) {
        if (radius == cornerRadius) {
            return
        }
        cornerRadius = radius
        val viewHeight = targetView.height
        if (viewHeight > 0) {
            realCornerRadius = if (cornerRadius > viewHeight / 2f) {
                viewHeight / 2f
            } else {
                cornerRadius
            }
        }
        targetView.invalidateOutline()
        internalStrokeDrawable?.cornerRadius = strokeCornerRadius
    }

    fun createBackgroundDrawable(): Drawable {
        /**
         * 1. backgroundDrawable
         *    1). RippleDrawable(pressedColor, backgroundDrawable, maskDrawable)
         *    2). contentBackgroundDrawable(外部传入)
         * 2. strokeDrawable
         * 3. LayerDrawable
         */
        val contentBackgroundDrawable = backgroundDrawable ?: GradientDrawable().apply {
            color = this@BaseStateBackgroundPresenter.backgroundColor
        }
        val strokeDrawable = strokeColor?.takeIf { strokeWidth > 0f }?.let {
            val drawable = GradientDrawable()
            drawable.cornerRadius = this@BaseStateBackgroundPresenter.strokeCornerRadius
            drawable.setColor(Color.TRANSPARENT)
            drawable.setStroke(strokeWidth.toInt(), strokeColor)
            return@let drawable
        }
        internalStrokeDrawable = strokeDrawable


        if (backgroundPressedColor.getColorForState(
                intArrayOf(android.R.attr.state_pressed), Color.TRANSPARENT
            ) == Color.TRANSPARENT
        ) {
            // 当 backgroundPressedColor 为透明时，不启用 RippleDrawable
            // 修复 Android 13 设置透明RippleDrawable，也会触发特效
            internalRippleMaskDrawable = null
            return if (strokeDrawable != null) {
                LayerDrawable(arrayOf(contentBackgroundDrawable, strokeDrawable))
            } else {
                contentBackgroundDrawable
            }
        } else {
            val maskDrawable = ColorDrawable(Color.WHITE)
            internalRippleMaskDrawable = maskDrawable
            val rippleDrawable = RippleDrawable(
                backgroundPressedColor, contentBackgroundDrawable, maskDrawable
            )
            return if (strokeDrawable != null) {
                LayerDrawable(arrayOf(rippleDrawable, strokeDrawable))
            } else {
                rippleDrawable
            }
        }
    }

    companion object {
        private const val CORNER_RADIUS_MAGIC = 1.0E-5f  // 0.00001
    }
}
