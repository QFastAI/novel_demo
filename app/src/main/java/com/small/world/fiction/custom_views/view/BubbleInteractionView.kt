package com.small.world.fiction.custom_views.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.small.world.fiction.custom_views.state.InteractionState

class BubbleInteractionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var lastX = 0f
    private var lastY = 0f
    private var isDragging = false

    private var isExpanded = false

    private var cx = 300f
    private var cy = 500f

    private var currentState = InteractionState.NORMAL

    // 气泡参数
    private var bubbleRadius = 80f
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#88C0FF")  // 默认蓝色
        style = Paint.Style.FILL
    }

    // 呼吸光圈
    private var glowAlpha = 0f
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#88C0FF")
        style = Paint.Style.FILL
    }

    private val glowAnimator = ValueAnimator.ofFloat(0.3f, 1f, 0.3f).apply {
        duration = 1500
        repeatCount = ValueAnimator.INFINITE
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            glowAlpha = it.animatedValue as Float
            invalidate()
        }
    }

    fun toggleState() {
        if (isExpanded) {
            collapse()
        } else {
            expand()
        }
    }

    fun isExpanded(): Boolean {
        return isExpanded
    }

    fun expand() {
        if (isExpanded) return
        isExpanded = true
        // 举例：执行展开动画
        animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).start()
    }

    fun collapse() {
        if (!isExpanded) return
        isExpanded = false
        // 举例：执行收起动画
        animate().scaleX(1f).scaleY(1f).setDuration(300).start()
    }

    // 加载旋转圈
    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private var loadingAngle = 0f
    private val loadingAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            loadingAngle = it.animatedValue as Float
            invalidate()
        }
    }

    // 闪烁动画（highlight 状态）
    private val flashAnimator = ObjectAnimator.ofArgb(
        bubblePaint, "color",
        Color.parseColor("#88C0FF"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#88C0FF")
    ).apply {
        duration = 800
        repeatCount = 2
        repeatMode = ValueAnimator.REVERSE
    }

    // 展开动画（放大）
    private val expandAnimator = ValueAnimator.ofFloat(80f, 140f).apply {
        duration = 400
        interpolator = OvershootInterpolator()
        addUpdateListener {
            bubbleRadius = it.animatedValue as Float
            invalidate()
        }
    }

    // 缩回动画
    private fun shrink() {
        ValueAnimator.ofFloat(bubbleRadius, 80f).apply {
            duration = 400
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                bubbleRadius = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    init {
        setOnClickListener {
            when (currentState) {
                InteractionState.NORMAL -> {
                    currentState = InteractionState.HIGHLIGHTED
                    flashAnimator.start()
                }
                InteractionState.HIGHLIGHTED -> {
                    currentState = InteractionState.EXPANDED
                    expandAnimator.start()
                    loadingAnimator.start()
                    glowAnimator.start()
                }
                InteractionState.EXPANDED -> {
                    currentState = InteractionState.SHRUNK
                    shrink()
                    loadingAnimator.cancel()
                    glowAnimator.cancel()
                }
                InteractionState.SHRUNK -> {
                    currentState = InteractionState.NORMAL
                    bubbleRadius = 80f
                    invalidate()
                }

                InteractionState.LOADING -> TODO()
                InteractionState.LOCKED -> TODO()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cx = width / 2f
        cy = height / 2f

        // 呼吸光圈（只有 EXPANDED 状态才有）
        if (currentState == InteractionState.EXPANDED) {
            glowPaint.alpha = (glowAlpha * 255).toInt()
            canvas.drawCircle(cx, cy, bubbleRadius + 25f, glowPaint)
        }

        // 主体气泡
        canvas.drawCircle(cx, cy, bubbleRadius, bubblePaint)

        // 加载动画（旋转弧形）
        if (currentState == InteractionState.EXPANDED) {
            val rect = RectF(
                cx - bubbleRadius + 20f,
                cy - bubbleRadius + 20f,
                cx + bubbleRadius - 20f,
                cy + bubbleRadius - 20f
            )
            canvas.drawArc(rect, loadingAngle, 120f, false, loadingPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                isDragging = false
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - lastX
                val dy = event.rawY - lastY

                if (!isDragging && (dx * dx + dy * dy > 100)) {
                    isDragging = true
                }

                if (isDragging) {
                    translationX += dx
                    translationY += dy
                    lastX = event.rawX
                    lastY = event.rawY
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isDragging) {
                    performClick()
                } else {
                    // 自动吸附到左右边缘
                    val parentWidth = (parent as? View)?.width ?: 0
                    val centerX = translationX + width / 2
                    val targetX = if (centerX < parentWidth / 2) 0f else (parentWidth - width).toFloat()
                    animate().translationX(targetX).setDuration(300).start()
                }
                isDragging = false
            }
        }
        return super.onTouchEvent(event)
    }

}
