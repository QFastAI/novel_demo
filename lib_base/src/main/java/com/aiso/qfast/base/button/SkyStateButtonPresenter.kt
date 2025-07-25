package com.aiso.qfast.base.button

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.SparseIntArray
import android.util.StateSet
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.aiso.qfast.base.R
import kotlin.math.min

class SkyStateButtonPresenter(private val button: TextView) : BaseStateBackgroundPresenter(button) {

    private var isRound = false
    private var textColor: ColorStateList = button.textColors

    private val attributeResIdMap = SparseIntArray()
    private val attributeColorValueMap = SparseIntArray()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            checkRoundRadius(bottom - top)
        }
    }

    @SuppressLint("NewApi")
    fun loadFromAttributes(attributes: TypedArray) {
        isRound = attributes.getBoolean(R.styleable.SkyStateButton_ss_round, false)
        cornerRadius = attributes.getDimensionPixelSize(R.styleable.SkyStateButton_ss_radius, 0)
            .toFloat()
        strokeWidth = attributes.getDimensionPixelSize(
            R.styleable.SkyStateButton_ss_stroke_width, 0
        ).toFloat()
        textColor = createColorStateList(
            getAttributesColor(
                attributes, R.styleable.SkyStateButton_ss_text_color, button.textColors.defaultColor
            ) ?: button.textColors.defaultColor,
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_text_color_selected),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_text_color_checked),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_text_color_activated),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_text_color_pressed),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_text_color_disable)
        )

        strokeColor = createColorStateList(
            getAttributesColor(
                attributes, R.styleable.SkyStateButton_ss_stroke_color, Color.TRANSPARENT
            ) ?: Color.TRANSPARENT,
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_stroke_color_selected),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_stroke_color_checked),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_stroke_color_activated),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_stroke_color_pressed),
            getAttributesColor(attributes, R.styleable.SkyStateButton_ss_stroke_color_disable)
        )

        val stateBackgroundNormalColor: Int = getAttributesColor(
            attributes, R.styleable.SkyStateButton_ss_background_color
        ) ?: Color.TRANSPARENT
        val stateBackgroundSelectColor: Int? = getAttributesColor(
            attributes, R.styleable.SkyStateButton_ss_background_color_selected
        )
        val stateBackgroundCheckedColor: Int? = getAttributesColor(
            attributes, R.styleable.SkyStateButton_ss_background_color_checked
        )
        val stateBackgroundActivatedColor: Int? = getAttributesColor(
            attributes, R.styleable.SkyStateButton_ss_background_color_activated
        )
        val stateBackgroundPressedColor: Int = getAttributesColor(
            attributes, R.styleable.SkyStateButton_ss_background_color_pressed, Color.TRANSPARENT
        ) ?: Color.TRANSPARENT
        val stateBackgroundDisableColor: Int? = getAttributesColor(
            attributes, R.styleable.SkyStateButton_ss_background_color_disable
        )

        backgroundColor = createColorStateList(
            stateBackgroundNormalColor,
            stateBackgroundSelectColor,
            stateBackgroundCheckedColor,
            stateBackgroundActivatedColor,
            null,
            stateBackgroundDisableColor
        )

        backgroundDrawable = getAttributesDrawable(
            attributes, R.styleable.SkyStateButton_ss_background_drawable
        )
        backgroundPressedColor = with(stateBackgroundPressedColor) {
            val states = arrayOf(intArrayOf(android.R.attr.state_pressed), StateSet.NOTHING)
            // ripple 的显示的背景颜色的是指定颜色的50 % 透明 。 所以需要有加深颜色
            val doubleAlphaColor = ColorUtils.setAlphaComponent(
                this, min(2 * Color.alpha(this), 255)
            )
            val colors = intArrayOf(doubleAlphaColor, doubleAlphaColor)
            ColorStateList(states, colors)
        }
    }

    @SuppressLint("NewApi")
    fun refreshAttributesColor() {
        textColor = createColorStateList(
            resolveAttributesColor(
                R.styleable.SkyStateButton_ss_text_color
            ) ?: button.textColors.defaultColor,
            resolveAttributesColor(R.styleable.SkyStateButton_ss_text_color_selected),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_text_color_checked),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_text_color_activated),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_text_color_pressed),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_text_color_disable)
        )

        strokeColor = createColorStateList(
            resolveAttributesColor(
                R.styleable.SkyStateButton_ss_stroke_color
            ) ?: Color.TRANSPARENT,
            resolveAttributesColor(R.styleable.SkyStateButton_ss_stroke_color_selected),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_stroke_color_checked),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_stroke_color_activated),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_stroke_color_pressed),
            resolveAttributesColor(R.styleable.SkyStateButton_ss_stroke_color_disable)
        )

        val stateBackgroundNormalColor: Int = resolveAttributesColor(
            R.styleable.SkyStateButton_ss_background_color
        ) ?: Color.TRANSPARENT
        val stateBackgroundSelectColor: Int? = resolveAttributesColor(
            R.styleable.SkyStateButton_ss_background_color_selected
        )
        val stateBackgroundCheckedColor: Int? = resolveAttributesColor(
            R.styleable.SkyStateButton_ss_background_color_checked
        )
        val stateBackgroundActivatedColor: Int? = resolveAttributesColor(
            R.styleable.SkyStateButton_ss_background_color_activated
        )
        val stateBackgroundPressedColor: Int = resolveAttributesColor(
            R.styleable.SkyStateButton_ss_background_color_pressed
        ) ?: Color.TRANSPARENT
        val stateBackgroundDisableColor: Int? = resolveAttributesColor(
            R.styleable.SkyStateButton_ss_background_color_disable
        )

        backgroundColor = createColorStateList(
            stateBackgroundNormalColor,
            stateBackgroundSelectColor,
            stateBackgroundCheckedColor,
            stateBackgroundActivatedColor,
            null,
            stateBackgroundDisableColor
        )
        backgroundDrawable =
            resolveAttributesDrawable(R.styleable.SkyStateButton_ss_background_drawable)
        backgroundPressedColor = with(stateBackgroundPressedColor) {
            val states = arrayOf(intArrayOf(android.R.attr.state_pressed), StateSet.NOTHING)
            // ripple 的显示的背景颜色的是指定颜色的50 % 透明 。 所以需要有加深颜色
            val doubleAlphaColor = ColorUtils.setAlphaComponent(
                this, min(2 * Color.alpha(this), 255)
            )
            val colors = intArrayOf(doubleAlphaColor, doubleAlphaColor)
            ColorStateList(states, colors)
        }
    }

    private fun getAttributesColor(
        attributes: TypedArray, index: Int, defaultColor: Int = Color.TRANSPARENT
    ): Int? {
        val resourceId = attributes.getResourceId(index, ResourcesCompat.ID_NULL)
        return when {
            resourceId != ResourcesCompat.ID_NULL -> {
                attributeResIdMap.put(index, resourceId)
                ContextCompat.getColor(button.context, resourceId)
            }

            attributes.hasValue(index) -> {
                attributes.getColor(index, defaultColor).also {
                    attributeColorValueMap.put(index, it)
                }
            }

            else -> {
                null
            }
        }
    }

    private fun resolveAttributesColor(index: Int): Int? {
        val resourceId = attributeResIdMap.get(index, ResourcesCompat.ID_NULL)
        if (resourceId != ResourcesCompat.ID_NULL) {
            return ContextCompat.getColor(button.context, resourceId)
        }
        // 不能用默认 0 ， 0 是透明色
        val colorValue = attributeColorValueMap.get(index, Int.MIN_VALUE)
        if (colorValue != Int.MIN_VALUE) {
            return colorValue
        }
        return null
    }

    private fun getAttributesDrawable(attributes: TypedArray, index: Int): Drawable? {
        val resourceId = attributes.getResourceId(index, ResourcesCompat.ID_NULL)
        return if (resourceId != ResourcesCompat.ID_NULL) {
            attributeResIdMap.put(index, resourceId)
            ContextCompat.getDrawable(button.context, resourceId)
        } else {
            null
        }
    }

    private fun resolveAttributesDrawable(index: Int): Drawable? {
        val resourceId = attributeResIdMap.get(index, ResourcesCompat.ID_NULL)
        return if (resourceId != ResourcesCompat.ID_NULL) {
            return ContextCompat.getDrawable(button.context, resourceId)
        } else {
            null
        }
    }

    @SuppressLint("NewApi")
    private fun createColorStateList(
        @ColorInt normalColor: Int,
        @ColorInt selectColor: Int?,
        @ColorInt checkedColor: Int?,
        @ColorInt activatedColor: Int?,
        @ColorInt pressedColor: Int?,
        @ColorInt disableColor: Int?
    ): ColorStateList {
        val stateList = ArrayList<IntArray>()
        val colorList = ArrayList<Int>()
        if (selectColor != null) {
            stateList.add(intArrayOf(android.R.attr.state_selected))
            colorList.add(selectColor)
        }
        if (checkedColor != null) {
            stateList.add(intArrayOf(android.R.attr.state_checked))
            colorList.add(checkedColor)
        }
        if (activatedColor != null) {
            stateList.add(intArrayOf(android.R.attr.state_activated))
            colorList.add(activatedColor)
        }
        if (pressedColor != null && pressedColor != Color.TRANSPARENT) {
            val fixPressedColor = if (Color.alpha(pressedColor) < 255) {
                // 处理 5.0 一下方案, 当有透明度时算出叠加色
                ColorUtils.compositeColors(pressedColor, normalColor)
            } else {
                pressedColor
            }
            stateList.add(intArrayOf(android.R.attr.state_pressed))
            colorList.add(fixPressedColor)
        }
        if (disableColor != null) {
            stateList.add(intArrayOf(-android.R.attr.state_enabled))
            colorList.add(disableColor)
        }
        stateList.add(StateSet.NOTHING)
        colorList.add(normalColor)
        return ColorStateList(stateList.toTypedArray(), colorList.toIntArray())
    }

    @SuppressLint("NewApi")
    fun bindStyle() {
        button.setTextColor(textColor)
        button.background = createBackgroundDrawable()
    }

    private fun checkRoundRadius(viewHeight: Int) {
        if (isRound) {
            updateRadius(viewHeight / 2f)
        }
    }

    fun changeTextColor(
        normalColor: Int,
        selectColor: Int? = null,
        checkedColor: Int? = null,
        activatedColor: Int? = null,
        pressedColor: Int? = null,
        disableColor: Int? = null
    ) {
        textColor = createColorStateList(
            normalColor, selectColor, checkedColor, activatedColor, pressedColor, disableColor
        )
    }

    fun changeBackgroundColor(
        normalColor: Int,
        selectColor: Int? = null,
        checkedColor: Int? = null,
        activatedColor: Int? = null,
        pressedColor: Int? = null,
        disableColor: Int? = null
    ) {
        backgroundColor = createColorStateList(
            normalColor, selectColor, checkedColor, activatedColor, pressedColor, disableColor
        )
    }

    fun changeBackgroundDrawable(drawable: Drawable) {
        backgroundDrawable = drawable
    }


    fun changeStrokeColor(
        normalColor: Int,
        selectColor: Int? = null,
        checkedColor: Int? = null,
        activatedColor: Int? = null,
        pressedColor: Int? = null,
        disableColor: Int? = null
    ) {
        strokeColor = createColorStateList(
            normalColor, selectColor, checkedColor, activatedColor, pressedColor, disableColor
        )
    }
}