package com.aiso.qfast.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.toColorInt

class ExpandableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var originalText: CharSequence = ""
    private var isExpanded = false
    private val maxCollapsedLines = 3
    private val expandText = "... 展开"
    private val collapseText = " 收起"
    private var layoutReady = false

    init {
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                if (!layoutReady && width > 0) {
                    layoutReady = true
                    resetText()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    fun setExpandableText(text: CharSequence) {
        originalText = text
        layoutReady = false
        text.let {
            super.setText(it, BufferType.NORMAL)
        }
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                if (!layoutReady && width > 0) {
                    layoutReady = true
                    resetText()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    private fun shouldCollapse(): Boolean {
        val layout = createStaticLayout(originalText)
        return layout.lineCount > maxCollapsedLines
    }

    private fun resetText() {
        if (!layoutReady) return

        val layout = createStaticLayout(originalText)
        val needsCollapse = layout.lineCount > maxCollapsedLines

        if (!needsCollapse) {
            text = originalText
            return
        }

        if (isExpanded) {
            val full = SpannableStringBuilder(originalText).append(collapseText)
            addClickableSpan(full, collapseText) {
                isExpanded = false
                resetText()
            }
            text = full
        } else {
            val thirdLineEnd = layout.getLineEnd(maxCollapsedLines - 1)
            val visibleText = originalText.subSequence(0, thirdLineEnd).toString()

            // 裁剪直到 "...展开" 不换行
            val availableWidth = width - paddingLeft - paddingRight
            val paint = paint
            var endIndex = visibleText.length

            while (endIndex > 0) {
                val test = visibleText.substring(0, endIndex) + expandText
                val testLayout = createStaticLayout(test)
                if (testLayout.lineCount <= maxCollapsedLines) break
                endIndex--
            }

            val finalText = visibleText.substring(0, endIndex) + expandText
            val spannable = SpannableStringBuilder(finalText)
            addClickableSpan(spannable, expandText) {
                isExpanded = true
                resetText()
            }
            text = spannable
        }
    }

    @SuppressLint("NewApi")
    private fun createStaticLayout(text: CharSequence): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width - paddingLeft - paddingRight)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            .setIncludePad(includeFontPadding)
            .build()
    }

    private fun addClickableSpan(
        builder: SpannableStringBuilder,
        keyword: String,
        onClick: () -> Unit
    ) {
        val start = builder.length - keyword.length
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) = onClick()
            override fun updateDrawState(ds: TextPaint) {
                ds.color = "#60FFFFFF".toColorInt()
                ds.isUnderlineText = false
            }
        }, start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

