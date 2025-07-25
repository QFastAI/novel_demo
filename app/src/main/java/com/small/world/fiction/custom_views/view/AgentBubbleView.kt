package com.small.world.fiction.custom_views.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.small.world.fiction.R
import kotlin.math.abs
import kotlin.math.hypot

class AgentBubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 气泡状态枚举
    enum class Mode {
        Idle, Loading, Options, Chatting
    }

    // 区域类型枚举
    enum class AreaType {
        None, Area1, Area2, Area3 // 根据实际需求定义区域
    }

    // 自定义属性
    private var bubbleRadius: Float = 60f
    private var bubbleColor: Int = Color.WHITE
    private var strokeWidth: Float = 4f
    private var strokeColor: Int = Color.BLUE
    private var highlightColor: Int = Color.GREEN
    private var loadingColor: Int = Color.CYAN
    private var defaultX: Float = 0f
    private var defaultY: Float = 0f

    // 绘制相关
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Align.CENTER
    }
    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    // 状态变量
    var currentMode: Mode = Mode.Idle
        set(value) {
            field = value
            invalidate()
        }
    private var isExpanded: Boolean = true
    private var activationProgress: Float = 0f
    private var rotationAngle: Float = 0f
    private var isDragging: Boolean = false
    private var currentX: Float = 0f
    private var currentY: Float = 0f
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var lastTouchTime: Long = 0
    private var currentArea: AreaType = AreaType.None
    private var isLongPressDetected: Boolean = false

    // 选项数据
    private var options: List<String> = listOf()
    private var selectedOptionIndex: Int = -1

    // 区域检测相关
    private val areaRects = mutableMapOf<AreaType, RectF>()

    init {
        // 初始化属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AgentBubbleView)
        bubbleRadius = typedArray.getDimension(R.styleable.AgentBubbleView_bubbleRadius, bubbleRadius)
        bubbleColor = typedArray.getColor(R.styleable.AgentBubbleView_bubbleColor, bubbleColor)
        strokeWidth = typedArray.getDimension(R.styleable.AgentBubbleView_strokeWidth, strokeWidth)
        strokeColor = typedArray.getColor(R.styleable.AgentBubbleView_strokeColor, strokeColor)
        highlightColor = typedArray.getColor(R.styleable.AgentBubbleView_highlightColor, highlightColor)
        loadingColor = typedArray.getColor(R.styleable.AgentBubbleView_loadingColor, loadingColor)
        defaultX = typedArray.getDimension(R.styleable.AgentBubbleView_defaultPositionX, 0f)
        defaultY = typedArray.getDimension(R.styleable.AgentBubbleView_defaultPositionY, 0f)
        typedArray.recycle()

        // 初始化位置
        currentX = defaultX
        currentY = defaultY

        // 初始化画笔
        strokePaint.strokeWidth = strokeWidth
        loadingPaint.strokeWidth = strokeWidth * 1.5f

        // 可以在这里初始化区域定义
        initAreas()
    }

    // 初始化交互区域
    private fun initAreas() {
        // 这里只是示例，实际应根据需求定义区域
        // 这些区域应该在onSizeChanged中重新计算
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 如果未设置默认位置，则将默认位置设置为中心
        if (defaultX == 0f && defaultY == 0f) {
            defaultX = w / 2f
            defaultY = h / 2f
            currentX = defaultX
            currentY = defaultY
        }

        // 重新计算交互区域
        areaRects.clear()
        // 示例：定义三个区域
        areaRects[AreaType.Area1] = RectF(0f, 0f, w / 3f, h.toFloat())
        areaRects[AreaType.Area2] = RectF(w / 3f, 0f, w * 2 / 3f, h.toFloat())
        areaRects[AreaType.Area3] = RectF(w * 2 / 3f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 根据当前模式绘制不同内容
        when (currentMode) {
            Mode.Idle -> drawIdleMode(canvas)
            Mode.Loading -> drawLoadingMode(canvas)
            Mode.Options -> drawOptionsMode(canvas)
            Mode.Chatting -> drawChattingMode(canvas)
        }
    }

    // 绘制空闲模式
    private fun drawIdleMode(canvas: Canvas) {
        drawBubbleBase(canvas)
        drawActivationState(canvas)
        canvas.drawText("A", currentX, currentY + textPaint.textSize / 4, textPaint)
    }

    // 绘制加载模式
    private fun drawLoadingMode(canvas: Canvas) {
        drawBubbleBase(canvas)

        // 绘制加载动画
        loadingPaint.color = loadingColor
        val bounds = RectF(
            currentX - bubbleRadius * 0.7f,
            currentY - bubbleRadius * 0.7f,
            currentX + bubbleRadius * 0.7f,
            currentY + bubbleRadius * 0.7f
        )
        canvas.drawArc(bounds, rotationAngle, 270f, false, loadingPaint)
    }

    // 绘制选项模式
    private fun drawOptionsMode(canvas: Canvas) {
        if (isExpanded) {
            // 绘制展开状态的选项
            drawOptionsBubble(canvas)
            drawOptionsList(canvas)
        } else {
            // 绘制缩略状态
            drawBubbleBase(canvas)
            canvas.drawText("O", currentX, currentY + textPaint.textSize / 4, textPaint)
        }
    }

    // 绘制聊天模式
    private fun drawChattingMode(canvas: Canvas) {
        if (isExpanded) {
            // 绘制聊天框
            drawChatBubble(canvas)
            drawChatContent(canvas)
        } else {
            // 绘制缩略状态
            drawBubbleBase(canvas)
            canvas.drawText("C", currentX, currentY + textPaint.textSize / 4, textPaint)
        }
    }

    // 绘制气泡基础形状
    private fun drawBubbleBase(canvas: Canvas) {
        // 绘制气泡填充
        bubblePaint.color = bubbleColor
        canvas.drawCircle(currentX, currentY, bubbleRadius, bubblePaint)

        // 绘制描边
        strokePaint.color = strokeColor
        canvas.drawCircle(currentX, currentY, bubbleRadius, strokePaint)
    }

    // 绘制激活状态效果
    private fun drawActivationState(canvas: Canvas) {
        if (activationProgress > 0) {
            when {
                activationProgress <= 0.1f -> {
                    // 0%-10% 只显示外轮廓的一点高光
                    strokePaint.color = highlightColor
                    val startAngle = 0f
                    val sweepAngle = 360f * activationProgress * 10
                    canvas.drawArc(
                        currentX - bubbleRadius * 1.2f,
                        currentY - bubbleRadius * 1.2f,
                        currentX + bubbleRadius * 1.2f,
                        currentY + bubbleRadius * 1.2f,
                        startAngle,
                        sweepAngle,
                        false,
                        strokePaint
                    )
                }
                activationProgress < 0.9f -> {
                    // 10%-90% 显示描边一圈
                    strokePaint.color = highlightColor
                    val alpha = (255 * (activationProgress - 0.1f) / 0.8f).toInt()
                    strokePaint.alpha = alpha
                    canvas.drawCircle(currentX, currentY, bubbleRadius * 1.2f, strokePaint)
                    strokePaint.alpha = 255
                }
                else -> {
                    // 90%-100% 显示描边确认（发光效果）
                    val glowRadius = bubbleRadius * 1.2f + (activationProgress - 0.9f) * 10
                    val alpha = 255 - ((activationProgress - 0.9f) * 10 * 155).toInt()

                    strokePaint.color = highlightColor
                    strokePaint.alpha = alpha
                    canvas.drawCircle(currentX, currentY, glowRadius, strokePaint)
                    strokePaint.alpha = 255
                }
            }
        }
    }

    // 绘制选项气泡
    private fun drawOptionsBubble(canvas: Canvas) {
        // 绘制气泡填充
        bubblePaint.color = bubbleColor
        canvas.drawRoundRect(
            currentX - bubbleRadius * 3,
            currentY - bubbleRadius * 2,
            currentX + bubbleRadius * 3,
            currentY + bubbleRadius * 2,
            20f, 20f, bubblePaint
        )

        // 绘制描边
        strokePaint.color = strokeColor
        canvas.drawRoundRect(
            currentX - bubbleRadius * 3,
            currentY - bubbleRadius * 2,
            currentX + bubbleRadius * 3,
            currentY + bubbleRadius * 2,
            20f, 20f, strokePaint
        )
    }

    // 绘制选项列表
    private fun drawOptionsList(canvas: Canvas) {
        if (options.isEmpty()) return

        // 绘制标题
        textPaint.textSize = 30f
        canvas.drawText("请选择:", currentX, currentY - bubbleRadius * 1.5f, textPaint)

        // 绘制选项
        textPaint.textSize = 24f
        val optionHeight = bubbleRadius * 0.8f
        options.forEachIndexed { index, option ->
            val yPos = currentY - bubbleRadius * 0.5f + index * optionHeight
            canvas.drawText(option, currentX, yPos, textPaint)

            // 绘制选中状态
            if (index == selectedOptionIndex) {
                canvas.drawCircle(
                    currentX - bubbleRadius * 2.5f,
                    yPos - textPaint.textSize / 2,
                    10f, bubblePaint
                )
                canvas.drawCircle(
                    currentX - bubbleRadius * 2.5f,
                    yPos - textPaint.textSize / 2,
                    10f, strokePaint
                )
            }
        }
    }

    // 绘制聊天气泡
    private fun drawChatBubble(canvas: Canvas) {
        // 绘制气泡填充
        bubblePaint.color = bubbleColor
        canvas.drawRoundRect(
            currentX - bubbleRadius * 3,
            currentY - bubbleRadius * 2,
            currentX + bubbleRadius * 3,
            currentY + bubbleRadius * 2,
            20f, 20f, bubblePaint
        )

        // 绘制描边
        strokePaint.color = strokeColor
        canvas.drawRoundRect(
            currentX - bubbleRadius * 3,
            currentY - bubbleRadius * 2,
            currentX + bubbleRadius * 3,
            currentY + bubbleRadius * 2,
            20f, 20f, strokePaint
        )
    }

    // 绘制聊天内容
    private fun drawChatContent(canvas: Canvas) {
        // 这里简化处理，实际应用中应该有更复杂的文本布局
        textPaint.textSize = 24f
        canvas.drawText("正在聊天中...", currentX, currentY, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                lastTouchTime = System.currentTimeMillis()
                isDragging = false
                isLongPressDetected = false

                // 检查是否点击在气泡上
                val distance = hypot(event.x - currentX, event.y - currentY)
                if (distance <= (if (isExpanded) bubbleRadius * 3 else bubbleRadius)) {
                    // 启动长按检测
                    postDelayed(longPressRunnable, 500) // 500ms后检测长按
                    return true
                }

                // 选项模式下检查是否点击了选项
                if (currentMode == Mode.Options && isExpanded) {
                    checkOptionClick(event.x, event.y)
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isLongPressDetected) return true

                val dx = event.x - downX
                val dy = event.y - downY

                // 判断是否开始拖拽
                if (!isDragging && hypot(dx, dy) > 20) { // 超过20像素认为是拖拽
                    isDragging = true
                    removeCallbacks(longPressRunnable) // 取消长按检测

                    // 展开态拖拽变为缩略态
                    if (isExpanded) {
                        isExpanded = false
                    }
                }

                // 处理拖拽
                if (isDragging) {
                    currentX = event.x
                    currentY = event.y

                    // 检测当前所在区域
                    checkCurrentArea()
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(longPressRunnable) // 取消长按检测

                if (isLongPressDetected) {
                    // 处理长按事件
                    handleLongPress()
                    isLongPressDetected = false
                } else if (isDragging) {
                    // 处理拖拽结束
                    handleDragEnd()
                    isDragging = false
                } else {
                    // 处理点击事件
                    handleClick()
                }

                invalidate()
            }
        }
        return true
    }

    // 长按检测Runnable
    private val longPressRunnable = Runnable {
        isLongPressDetected = true
        isDragging = false
        // 长按开始，可以显示反馈
        activationProgress = 0.1f
        invalidate()
    }

    // 检查当前所在区域
    private fun checkCurrentArea() {
        currentArea = AreaType.None
        for ((type, rect) in areaRects) {
            if (rect.contains(currentX, currentY)) {
                currentArea = type
                break
            }
        }
    }

    // 检查选项点击
    private fun checkOptionClick(x: Float, y: Float) {
        if (options.isEmpty()) return

        val optionHeight = bubbleRadius * 0.8f
        options.forEachIndexed { index, _ ->
            val yPos = currentY - bubbleRadius * 0.5f + index * optionHeight
            val rect = RectF(
                currentX - bubbleRadius * 3,
                yPos - textPaint.textSize,
                currentX + bubbleRadius * 3,
                yPos
            )
            if (rect.contains(x, y)) {
                selectedOptionIndex = index
                onOptionSelectedListener?.onOptionSelected(index, options[index])
                invalidate()
            }
        }
    }

    // 处理拖拽结束
    private fun handleDragEnd() {
        // 检查在区域内停留时间
        val currentTime = System.currentTimeMillis()
        val stayTime = currentTime - lastTouchTime

        if (currentArea != AreaType.None && stayTime > 1000) { // 在区域内停留超过1秒
            // 触发区域特有AI编辑动作
            onAreaActionTriggeredListener?.onAreaActionTriggered(currentArea)
        }

        // 如果是缩略态，拖拽结束后不自动展开
        // 如果是在选项或聊天模式的缩略态，点击后才展开
    }

    // 处理点击事件
    private fun handleClick() {
        if (currentMode == Mode.Options || currentMode == Mode.Chatting) {
            if (!isExpanded) {
                // 单击缩略态回到默认位置并展开
                animateToDefaultPosition()
                isExpanded = true
            }
        } else {
            // 其他模式的点击处理
            onBubbleClickListener?.onClick()
        }
    }

    // 处理长按事件
    private fun handleLongPress() {
        if (currentArea != AreaType.None) {
            // 触发该区域的AI响应
            onAreaLongPressListener?.onAreaLongPressed(currentArea)
        }
    }

    // 动画移动到默认位置
    private fun animateToDefaultPosition() {
        val animatorX = ValueAnimator.ofFloat(currentX, defaultX)
        val animatorY = ValueAnimator.ofFloat(currentY, defaultY)

        animatorX.duration = 300
        animatorY.duration = 300
        animatorX.interpolator = DecelerateInterpolator()
        animatorY.interpolator = DecelerateInterpolator()

        animatorX.addUpdateListener { animation ->
            currentX = animation.animatedValue as Float
            invalidate()
        }

        animatorY.addUpdateListener { animation ->
            currentY = animation.animatedValue as Float
            invalidate()
        }

        animatorX.start()
        animatorY.start()
    }

    // 启动加载动画
    fun startLoadingAnimation() {
        currentMode = Mode.Loading
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = 1500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animation ->
            rotationAngle = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    // 设置激活进度
    fun setActivationProgress(progress: Float) {
        activationProgress = progress.coerceIn(0f, 1f)
        invalidate()
    }

    // 设置选项数据
    fun setOptions(optionsList: List<String>) {
        options = optionsList
        selectedOptionIndex = -1
        currentMode = Mode.Options
        isExpanded = true
        invalidate()
    }

    // 切换到聊天模式
    fun switchToChattingMode() {
        currentMode = Mode.Chatting
        isExpanded = true
        invalidate()
    }

    // 切换到空闲模式
    fun switchToIdleMode() {
        currentMode = Mode.Idle
        invalidate()
    }

    // 回调接口
    interface OnBubbleClickListener {
        fun onClick()
    }

    interface OnOptionSelectedListener {
        fun onOptionSelected(index: Int, option: String)
    }

    interface OnAreaActionTriggeredListener {
        fun onAreaActionTriggered(areaType: AreaType)
    }

    interface OnAreaLongPressListener {
        fun onAreaLongPressed(areaType: AreaType)
    }

    var onBubbleClickListener: OnBubbleClickListener? = null
    var onOptionSelectedListener: OnOptionSelectedListener? = null
    var onAreaActionTriggeredListener: OnAreaActionTriggeredListener? = null
    var onAreaLongPressListener: OnAreaLongPressListener? = null
}