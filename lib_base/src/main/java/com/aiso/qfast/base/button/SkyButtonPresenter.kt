package com.aiso.qfast.base.button

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.TextUtils
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.aiso.qfast.base.R

class SkyButtonPresenter(private val button: TextView) {
    internal var textAutoCenter = true

    // 默认实际读取  button.includeFontPadding = true
    internal var includeFontPadding = false
    internal var disableIcon = false

    private var icon1: Drawable? = null
    private var icon1Tint: ColorStateList? = null
    private var icon1Width: Int = 0
    private var icon1Height: Int = 0
    private var icon1Gravity: Int = ICON_GRAVITY_DEFAULT
    private var icon1Offset: Int = 0
    private var icon1Res: Int = 0
    private var icon1TintRes: Int? = 0

    private var icon2: Drawable? = null
    private var icon2Tint: ColorStateList? = null
    private var icon2Width: Int = 0
    private var icon2Height: Int = 0
    private var icon2Gravity: Int = ICON_GRAVITY_DEFAULT
    private var icon2Offset: Int = 0
    private var icon2Res: Int = 0
    private var icon2TintRes: Int? = 0

    @SuppressLint("NewApi")
    internal fun loadFromAttributes(attributes: TypedArray) {
        textAutoCenter = attributes.getBoolean(R.styleable.SkyButton_btn_auto_text_center, true)
        includeFontPadding = attributes.getBoolean(
            R.styleable.SkyButton_btn_include_font_padding, button.includeFontPadding
        )
        disableIcon = attributes.getBoolean(R.styleable.SkyButton_btn_disable_icon, false)
        icon1Res = attributes.getResourceId(R.styleable.SkyButton_btn_icon1, 0)
        icon1 = getResIdDrawable(icon1Res)
        icon1TintRes = attributes.getResourceId(R.styleable.SkyButton_btn_icon1_tint, 0)
        icon1Tint = attributes.getColorStateList(R.styleable.SkyButton_btn_icon1_tint)
        icon1Width = attributes.getDimensionPixelSize(R.styleable.SkyButton_btn_icon1_width, 0)
        icon1Height = attributes.getDimensionPixelSize(R.styleable.SkyButton_btn_icon1_height, 0)
        icon1Gravity = attributes.getInt(
            R.styleable.SkyButton_btn_icon1_gravity, ICON_GRAVITY_DEFAULT
        )

        icon2Res = attributes.getResourceId(R.styleable.SkyButton_btn_icon2, 0)
        icon2 = getResIdDrawable(icon2Res)
        icon2TintRes = attributes.getResourceId(R.styleable.SkyButton_btn_icon2_tint, 0)
        icon2Tint = attributes.getColorStateList(R.styleable.SkyButton_btn_icon2_tint)
        icon2Width = attributes.getDimensionPixelSize(R.styleable.SkyButton_btn_icon2_width, 0)
        icon2Height = attributes.getDimensionPixelSize(R.styleable.SkyButton_btn_icon2_height, 0)
        icon2Gravity = attributes.getInt(
            R.styleable.SkyButton_btn_icon2_gravity, ICON_GRAVITY_DEFAULT
        )
    }

    internal fun getResIdDrawable(resId: Int?): Drawable? {
        return if (resId != null && resId != 0) {
            ContextCompat.getDrawable(button.context, resId)
        } else {
            null
        }
    }

    internal fun getResIdTint(resId: Int?): ColorStateList? {
        return if (resId != null && resId != 0) {
            ContextCompat.getColorStateList(button.context, resId)
        } else {
            null
        }
    }

    fun setIcon1(
        drawable: Drawable? = null,
        width: Int = 0,
        height: Int = 0,
        tint: ColorStateList? = null,
        @IconGravity gravity: Int? = null
    ) {
        icon1Res = 0
        icon1 = drawable

        if (width > 0) {
            icon1Width = width
        }
        if (height > 0) {
            icon1Height = height
        }
        if (tint != null) {
            icon1Tint = tint
            icon1TintRes = 0
        }
        if (gravity != null) {
            icon1Gravity = gravity
        }
        updateIcon()
    }

    fun setIcon2(
        drawable: Drawable? = null,
        width: Int = 0,
        height: Int = 0,
        tint: ColorStateList? = null,
        @IconGravity gravity: Int? = null
    ) {
        icon2Res = 0
        icon2 = drawable

        if (width > 0) {
            icon2Width = width
        }
        if (height > 0) {
            icon2Height = height
        }
        if (tint != null) {
            icon2Tint = tint
            icon2TintRes = 0
        }
        if (gravity != null) {
            icon2Gravity = gravity
        }
        updateIcon()
    }

    internal fun updateIcon() {
        if (disableIcon) {
            return
        }

        val yOffset = if (includeFontPadding) {
            val metrics = button.paint.fontMetrics
            (((metrics.bottom - metrics.top) - (metrics.descent - metrics.ascent)) / 2f).toInt()
        } else {
            0
        }

        val leftDrawable = with(icon1Tint) {
            if (this != null) {
                icon1.tint(this)
            } else {
                icon1
            }
        }?.apply {
            val width = if (icon1Width > 0) icon1Width else intrinsicWidth
            val height = if (icon1Height > 0) icon1Height else intrinsicHeight
            setBounds(icon1Offset, yOffset, icon1Offset + width, height + yOffset)
        }

        val rightDrawable = with(icon2Tint) {
            if (this != null) {
                icon2.tint(this)
            } else {
                icon2
            }
        }?.apply {
            val width = if (icon2Width > 0) icon2Width else intrinsicWidth
            val height = if (icon2Height > 0) icon2Height else intrinsicHeight
            setBounds(icon2Offset, yOffset, width + icon2Offset, height + yOffset)
        }

        TextViewCompat.setCompoundDrawablesRelative(
            button, leftDrawable, null, rightDrawable, null
        )
    }

    fun rebindStyle() {
        if (disableIcon) {
            return
        }

        val newIcon1 = getResIdDrawable(icon1Res) ?: icon1
        if (newIcon1 != null) {
            icon1 = newIcon1
        }
        val newTint1 = getResIdTint(icon1TintRes)
        if (newTint1 != null) {
            icon1Tint = newTint1
        }

        val newIcon2 = getResIdDrawable(icon2Res)
        if (newIcon2 != null) {
            icon2 = newIcon2
        }

        val newTint2 = getResIdTint(icon2TintRes)
        if (newTint2 != null) {
            icon2Tint = newTint2
        }
        updateIcon()
    }

    internal fun measure() {
        if (!textAutoCenter || disableIcon) {
            return
        }

        if (icon1Gravity != ICON_GRAVITY_TEXT && icon2Gravity != ICON_GRAVITY_TEXT) {
            return
        }

        val iconPadding = button.compoundDrawablePadding

        val icon1Width = icon1?.let {
            if (icon1Width > 0) {
                icon1Width + iconPadding
            } else {
                it.intrinsicWidth + iconPadding
            }
        } ?: 0

        val icon2Width = icon2?.let {
            if (icon2Width > 0) {
                icon2Width + iconPadding
            } else {
                it.intrinsicWidth + iconPadding
            }
        } ?: 0

        val measureText = TextUtils.ellipsize(
            button.text,
            button.paint,
            (button.measuredWidth - button.paddingLeft - button.paddingRight - icon1Width - icon2Width).toFloat(),
            TextUtils.TruncateAt.END
        )

        val textWidth = (Layout.getDesiredWidth(measureText, button.paint) + 0.5f).toInt()

        val restSpace =
            button.measuredWidth - textWidth - button.paddingLeft - button.paddingRight - icon1Width - icon2Width

        var newIcon1Offset = 0
        var newIcon2Offset = 0
        if (icon1Width > 0 && icon2Width > 0) {
            // 同时有 icon1 和 icon2
            newIcon1Offset = restSpace / 2
            newIcon2Offset = -newIcon1Offset
        } else if (icon1Width > 0) {
            // 只有 icon1
            newIcon1Offset = restSpace / 2
            newIcon2Offset = 0
        } else if (icon2Width > 0) {
            // 只有 icon2
            newIcon1Offset = 0
            newIcon2Offset = -restSpace / 2
        }

        if (newIcon1Offset != icon1Offset || newIcon2Offset != icon2Offset) {
            icon1Offset = newIcon1Offset
            icon2Offset = newIcon2Offset
            updateIcon()
        }
    }

    companion object {
        const val ICON_GRAVITY_DEFAULT = 1
        const val ICON_GRAVITY_TEXT = 2

        @IntDef(
            ICON_GRAVITY_DEFAULT, ICON_GRAVITY_TEXT
        )
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class IconGravity
    }
}