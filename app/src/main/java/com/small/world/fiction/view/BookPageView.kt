package com.small.world.fiction.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.aiso.qfast.utils.LogUtils
import kotlin.math.abs
class PageReaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 50f // 正文字号
    }

    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = textPaint.textSize + 4f // 标题字号大4
        typeface = Typeface.DEFAULT_BOLD
    }

    private var titleText: String = ""
    private var chapterText: String = ""

    private val pages = mutableListOf<StaticLayout>()
    private var currentPage = 0
    private var pageChangeListener: ((Int, Int) -> Unit)? = null

    private val lineSpacing = 20
    private val padding = 50

    private val gestureDetector = GestureDetector(context, GestureListener())

    private var animating = false
    private var animationOffset = 0f
    private var animationStartPage = 0
    private var animationEndPage = 0

    init {
        isFocusable = true
        isClickable = true
    }

    fun setTitleAndText(title: String, text: String,defaultPage: Int = 0) {
        this.titleText = title
        this.chapterText = text
        post {
            paginateText()
            currentPage = defaultPage.coerceIn(0, pages.size - 1)
            notifyPageChange()
            invalidate()
        }
    }

    private fun paginateText() {
        pages.clear()

        val width = width - padding * 2
        val height = height - padding * 2

        // 标题预排，确定高度
        val titleLayout = StaticLayout.Builder.obtain(titleText, 0, titleText.length, titlePaint, width)
            .setIncludePad(false)
            .build()
        val titleHeight = titleLayout.height + 30 // 标题与正文间距

        var start = 0
        while (start < chapterText.length) {
            val layout = StaticLayout.Builder.obtain(
                chapterText, start, chapterText.length, textPaint, width
            ).setLineSpacing(lineSpacing.toFloat(), 1f)
                .setIncludePad(false)
                .build()

            var linesHeight = titleHeight
            var end = start
            for (i in 0 until layout.lineCount) {
                linesHeight += layout.getLineBottom(i) - layout.getLineTop(i)
                if (linesHeight > height) {
                    end = layout.getLineEnd(i - 1)
                    break
                } else {
                    end = layout.getLineEnd(i)
                }
            }

            val pageLayout = StaticLayout.Builder.obtain(
                chapterText, start, end, textPaint, width
            ).setLineSpacing(lineSpacing.toFloat(), 1f)
                .setIncludePad(false)
                .build()

            pages.add(pageLayout)
            start = end
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(padding.toFloat(), padding.toFloat())

        val viewWidth = width.toFloat()

        if (animating) {
            canvas.save()
            canvas.translate(animationOffset, 0f)
            drawPage(canvas, animationStartPage)
            canvas.restore()

            canvas.save()
            canvas.translate(animationOffset + if (animationOffset < 0) viewWidth else -viewWidth, 0f)
            drawPage(canvas, animationEndPage)
            canvas.restore()
        } else {
            drawPage(canvas, currentPage)
        }

        canvas.restore()
    }

    private fun drawPage(canvas: Canvas, pageIndex: Int) {
        val width = width - padding * 2

        var titleHeight = 0f
        // 画标题
        if (pageIndex == 0) {
            // 仅第一页绘制标题
            val titleLayout = StaticLayout.Builder.obtain(titleText, 0, titleText.length, titlePaint, width)
                .setIncludePad(false)
                .build()
            titleLayout.draw(canvas)
            titleHeight = titleLayout.height + 30f
        }

        // 画正文
        canvas.translate(0f, titleHeight + 30f)
        if (pages.indices.contains(pageIndex)) {
            pages[pageIndex].draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun toNextPage() {
        if (currentPage < pages.size - 1 && !animating) {
            animatePageTurn(currentPage, currentPage + 1, forward = true)
        }
        if(currentPage == pages.size - 1){
            onEndNextEvent.invoke()
        }
    }

    var onEndNextEvent: () -> Unit = {}

    private fun toPreviousPage() {
        if (currentPage > 0 && !animating) {
            animatePageTurn(currentPage, currentPage - 1, forward = false)
        }
    }

    private fun animatePageTurn(from: Int, to: Int, forward: Boolean) {
        animating = true
        animationStartPage = from
        animationEndPage = to
        val width = width.toFloat()
        val start = 0f
        val end = if (forward) -width else width

        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            animationOffset = it.animatedValue as Float
            invalidate()
        }
        animator.doOnEnd {
            currentPage = to
            animating = false
            animationOffset = 0f
            notifyPageChange()
            invalidate()
        }
        animator.start()
    }

    private fun notifyPageChange() {
        pageChangeListener?.invoke(currentPage + 1, pages.size)
    }

    fun setOnPageChangeListener(listener: (current: Int, total: Int) -> Unit) {
        this.pageChangeListener = listener
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null && e2 != null) {
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                    if (diffX > 0) {
                        toPreviousPage()
                    } else {
                        toNextPage()
                    }
                    return true
                }
            }
            return false
        }
    }
}



