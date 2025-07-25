package com.aiso.qfast.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.aiso.qfast.base.R
import java.lang.reflect.Field

class CustomDatePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DatePicker(context, attrs, defStyleAttr) {

    private val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    init {
        initCustomSettings()
    }

    @SuppressLint("PrivateApi", "NewApi")
    private fun initCustomSettings() {
        // 隐藏日历视图，仅显示滚轮
        calendarViewShown = false

        // 设置 spinner 模式（部分机型需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val modeField = DatePicker::class.java.getDeclaredField("mMode")
                modeField.isAccessible = true
                modeField.set(this, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        customizeNumberPickers()
        setMonthDisplayedValues() // 设置英文月份
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        distributeWidthEqually()
    }

    @SuppressLint("PrivateApi")
    private fun distributeWidthEqually() {
        try {
            val spinnersField = DatePicker::class.java.getDeclaredField("mSpinners")
            spinnersField.isAccessible = true
            val spinners = spinnersField.get(this) as? LinearLayout

            spinners?.let { layout ->
                val displayMetrics = context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                val padding = (layout.paddingLeft + layout.paddingRight)
                val availableWidth = screenWidth - padding
                val childWidth = availableWidth / 3

                for (i in 0 until layout.childCount) {
                    val child = layout.getChildAt(i)
                    child.layoutParams = LinearLayout.LayoutParams(
                        childWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                    }
                }

                layout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                layout.setPadding(0, layout.paddingTop, 0, layout.paddingBottom)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "NewApi")
    private fun customizeNumberPickers() {
        try {
            val spinnersField = DatePicker::class.java.getDeclaredField("mSpinners")
            spinnersField.isAccessible = true
            val spinners = spinnersField.get(this) as? LinearLayout

            spinners?.let {
                it.dividerDrawable = null
                it.showDividers = LinearLayout.SHOW_DIVIDER_NONE

                for (i in 0 until it.childCount) {
                    val child = it.getChildAt(i)
                    if (child is NumberPicker) {
                        customizeSingleNumberPicker(child)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "NewApi")
    private fun setMonthDisplayedValues() {
        try {
            val monthField = DatePicker::class.java.getDeclaredField("mMonthSpinner")
            monthField.isAccessible = true
            val monthPicker = monthField.get(this) as? NumberPicker

            monthPicker?.apply {
                minValue = 0
                maxValue = 11
                displayedValues = monthNames
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "NewApi")
    private fun customizeSingleNumberPicker(numberPicker: NumberPicker) {
        try {
            val selectionDivider = NumberPicker::class.java.getDeclaredField("mSelectionDivider")
            selectionDivider.isAccessible = true
            selectionDivider.set(numberPicker, null)

            val selectorPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
            selectorPaintField.isAccessible = true
            (selectorPaintField.get(numberPicker) as? android.graphics.Paint)?.color =
                ContextCompat.getColor(context, R.color.color_575757)

            val selectionDividerHeight = NumberPicker::class.java.getDeclaredField("mSelectionDividerHeight")
            selectionDividerHeight.isAccessible = true
            selectionDividerHeight.setInt(numberPicker, 0)

            val inputText = NumberPicker::class.java.getDeclaredField("mInputText")
            inputText.isAccessible = true
            (inputText.get(numberPicker) as? EditText)?.apply {
                setTextColor(ContextCompat.getColor(context, R.color.white))
                textSize = 18f
                isCursorVisible = false
                isFocusable = false
                isFocusableInTouchMode = false
                background = null
            }

            // 去除上下两边的选择线（API 30以下）
            val selectorTextColors = NumberPicker::class.java.getDeclaredField("mSelectorTextColor")
            selectorTextColors.isAccessible = true
            selectorTextColors.set(numberPicker, ContextCompat.getColorStateList(context, R.color.color_40FFFFFF))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
