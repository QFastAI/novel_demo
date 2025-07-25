package com.aiso.qfast.base.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Outline
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.Layout
import android.text.Spanned
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.Window
import android.view.animation.CycleInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aiso.qfast.base.ext.IMEWindowInsetsAnimationListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.internal.ViewUtils.dpToPx
import timber.log.Timber
import java.lang.reflect.Field
import kotlin.math.hypot

/**
 * 绘制之前调用
 * 返回值onPreDraw:是否继续绘制，修改宽高的等需要返回false
 *
 * ViewTreeObserver.OnPreDrawListener 的返回值是一个布尔值。
 * 如果返回 true，则表示绘制事件会继续进行；
 * 如果返回 false，则表示绘制事件会被取消，视图树将不会绘制。
 *通过返回 false，你可以在视图树绘制之前进行一些操作，例如修改视图的属性或者执行一些动画效果。
 * 如果你需要在绘制之前做一些准备工作或者对绘制进行干预，可以使用 ViewTreeObserver.OnPreDrawListener 来实现。
 */
inline fun View.onPreDrawListener(crossinline onPreDraw: ((View) -> Boolean)) {
    val mPreDraw = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            return onPreDraw.invoke(this@onPreDrawListener)
        }
    }
    viewTreeObserver.addOnPreDrawListener(mPreDraw)
}


inline fun View.onGlobalLayoutListener(crossinline doGlobalLayout: ((View) -> Unit)) {
    val mGlobalLayout = object : ViewTreeObserver.OnGlobalLayoutListener {
        @SuppressLint("NewApi")
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            doGlobalLayout.invoke(this@onGlobalLayoutListener)
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayout)
}

/**
 * view 设置margin
 */
inline fun View.setMargins(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
): View {
    val tmp = layoutParams
    if (tmp is MarginLayoutParams) {
        tmp.setMargins(left, top, right, bottom)
    }
    return this
}


/**
 * view 设置margin
 */
inline fun View.removeParent() = kotlin.runCatching { getParentView()?.removeView(this) }

/**
 * 触摸的点是否在某个view上
 */
fun MotionEvent.touchInView(view: View): Boolean {
    // 获取触摸点的坐标
    val touchX = this.rawX.toInt()
    val touchY = this.rawY.toInt()

    // 获取View的位置
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val viewX = location[0]
    val viewY = location[1]

    // 获取View的宽度和高度
    val width = view.width
    val height = view.height

    // 判断触摸点是否在View的范围内
    return touchX >= viewX && touchX <= viewX + width && touchY >= viewY && touchY <= viewY + height
}

/*获取可见百分比
@param v
@return
*/
fun View.getVisiblePercent(): Int {
    val r = Rect()
    val visible = getLocalVisibleRect(r)
    if (visible && measuredHeight > 0) {
        val percent = 100 * r.height() / measuredHeight
        return percent
    }
    return -1
}
inline fun View.adjustAspectRatio(widthRatio: Number, heightRatio: Number) {
    kotlin.runCatching {
        val layoutParams = layoutParams
        if (layoutParams is MarginLayoutParams) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = (layoutParams.width * (heightRatio.toDouble() / widthRatio.toDouble())).toInt()
            this.layoutParams = layoutParams
        }
    }
}

@SuppressLint("NewApi")
fun View.setRoundCorners(radius: Int) {
    outlineProvider = object : ViewOutlineProvider() {
        @SuppressLint("RestrictedApi")
        override fun getOutline(view: View, outline: Outline) {
            val cornerRadius = dpToPx(view.context, radius).toFloat()
            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
        }
    }
    clipToOutline = true
}

/**
 * view 设置margin
 */
inline fun View?.getParentView(): ViewGroup? = (this?.parent as? ViewGroup)


fun View.androidContent(): View = rootView.findViewById(android.R.id.content)


//val View.parentView: ViewGroup? get()= (this?.parent as? ViewGroup)

fun View.compareAndSetLayoutHeight(newHeight: Int) {
    if (layoutParams.height != newHeight) {
        updateLayoutParams<ViewGroup.LayoutParams> {
            this.height = newHeight
        }
    }
}

fun View.compareAndSetLayoutWidth(newWidth: Int) {
    if (layoutParams.width != newWidth) {
        updateLayoutParams<ViewGroup.LayoutParams> {
            this.width = newWidth
        }
    }
}

fun BottomSheetBehavior<out View>.compareAndSetPeekHeight(newPeekHeight: Int) {
    if (peekHeight != newPeekHeight) {
        peekHeight = newPeekHeight
    }
}

@SuppressLint("NewApi")
@JvmOverloads
fun View.alphaVisible(duration: Long = 300L, endListener: (() -> Unit)? = null) {
    visibility = View.VISIBLE
    alpha = 0f
    val animator = animate().alpha(1f).setDuration(duration)
    if (endListener != null) {
        animator.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                endListener.invoke()
            }
        })
    }
    animator.start()
}

@SuppressLint("NewApi")
@JvmOverloads
fun View.alphaGone(duration: Long = 300L, endListener: (() -> Unit)? = null) {
    val animator = animate().alpha(0f).setDuration(duration)
    animator.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            visibility = View.GONE
            endListener?.invoke()
        }
    })
    animator.start()
}

@SuppressLint("NewApi")
@JvmOverloads
fun View.alphaInvisible(duration: Long = 300L, endListener: (() -> Unit)? = null) {
    val animator = animate().alpha(0f).setDuration(duration)
    animator.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            visibility = View.INVISIBLE
            endListener?.invoke()
        }
    })
    animator.start()
}

@SuppressLint("NewApi")
@JvmOverloads
fun View.flexScale(scaleFactor: Float, duration: Long = 300L, endListener: (() -> Unit)? = null) {
    val animator = animate().scaleX(scaleFactor).scaleY(scaleFactor).setDuration(duration)
        .setInterpolator(CycleInterpolator(0.5f))
    if (endListener != null) {
        animator.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                endListener.invoke()
            }
        })
    }
    animator.start()
}

@SuppressLint("NewApi")
@JvmOverloads
fun View.circularReveal(
    revealPoint: PointF, visible: Boolean, duration: Long = 300L, endListener: (() -> Unit)? = null
) {
    if (visible) {
        visibility = View.VISIBLE
    }
    val radius = hypot(revealPoint.x, (height - revealPoint.y).coerceAtLeast(revealPoint.y))
    val startRadius = if (visible) 0f else radius
    val endRadius = if (visible) radius else 0f
    val animator = ViewAnimationUtils.createCircularReveal(
        this, revealPoint.x.toInt(), revealPoint.y.toInt(), startRadius, endRadius
    ).apply {
        this.duration = duration
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                if (!visible) {
                    visibility = View.INVISIBLE
                }
                endListener?.invoke()
            }
        })
    }
    animator.start()
}

fun View.getRectOnScreen(): Rect {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return Rect(
        location[0],
        location[1],
        location[0] + width,
        location[1] + height,
    )
}

fun View.getRectInWindow(): Rect {
    val location = IntArray(2)
    getLocationInWindow(location)
    return Rect(
        location[0],
        location[1],
        location[0] + width,
        location[1] + height,
    )
}

fun View.doOnApplyWindowInsets(callback: (View, WindowInsetsCompat) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this, OnApplyWindowInsetsListener { v, insets ->
        callback(v, insets)
        return@OnApplyWindowInsetsListener insets
    })
    requestApplyInsetsWhenAttached(this)
}

fun View.doOnApplyWindowInsetsAnimations(listener: IMEWindowInsetsAnimationListener) {
    ViewCompat.setOnApplyWindowInsetsListener(this, listener)
    ViewCompat.setWindowInsetsAnimationCallback(this, listener)
    requestApplyInsetsWhenAttached(this)
}

fun View.doOnApplyWindowInsetsAnimations(
    windowInsetsListener: (windowInsets: WindowInsetsCompat, isImeVisible: Boolean, maxInsetBottom: Int) -> Unit,
    onAnimationPrepare: ((windowInsets: WindowInsetsCompat, isImeVisible: Boolean, maxInsetBottom: Int) -> Unit)? = null,
    onAnimationProgress: ((isImeVisible: Boolean, animationMaxInsetBottom: Int, animationHeight: Int, diffY: Int) -> Unit)? = null,
    onAnimationEnd: (() -> Unit)? = null
) {
    doOnApplyWindowInsetsAnimations(object : IMEWindowInsetsAnimationListener() {
        override fun onWindowInsetsListener(
            windowInsets: WindowInsetsCompat, isImeVisible: Boolean, maxInsetBottom: Int
        ) {
            windowInsetsListener.invoke(windowInsets, isImeVisible, maxInsetBottom)
        }

        override fun onAnimationPrepare(
            windowInsets: WindowInsetsCompat, isImeVisible: Boolean, maxInsetBottom: Int
        ) {
            onAnimationPrepare?.invoke(windowInsets, isImeVisible, maxInsetBottom)
        }

        override fun onAnimationProgress(
            isImeVisible: Boolean, animationMaxInsetBottom: Int, animationHeight: Int, diffY: Int
        ) {
            onAnimationProgress?.invoke(
                isImeVisible, animationMaxInsetBottom, animationHeight, diffY
            )
        }

        override fun onAnimationEnd() {
            onAnimationEnd?.invoke()
        }
    })
}

@SuppressLint("NewApi")
private fun requestApplyInsetsWhenAttached(view: View) {
    if (view.isAttachedToWindow) {
        ViewCompat.requestApplyInsets(view)
    } else {
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(view)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun RecyclerView.forceClearRecycler() {
    try {
        var recyclerViewClass: Class<Any>? = javaClass
        while (recyclerViewClass != null) {
            if (recyclerViewClass.name == RecyclerView::class.java.name) {
                break
            }
            recyclerViewClass = recyclerViewClass.superclass
        }
        recyclerViewClass ?: return

        val declaredField = recyclerViewClass.getDeclaredField("mRecycler")
        declaredField.isAccessible = true
        val declaredMethod = Class.forName(RecyclerView.Recycler::class.java.name)
            .getDeclaredMethod("clear")
        declaredMethod.isAccessible = true
        declaredMethod.invoke(declaredField.get(this))

        recycledViewPool.clear()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ViewPager.setupAdapter(adapter: PagerAdapter?, targetPosition: Int?) {
    var position: Int? = null
    if (adapter != null && targetPosition != null) {
        position = 0.coerceAtLeast(targetPosition.coerceAtMost(adapter.count - 1))
        if (position != 0) {
            try {
                val c = Class.forName("androidx.viewpager.widget.ViewPager")
                val field: Field = c.getDeclaredField("mCurItem")
                field.isAccessible = true
                field.setInt(this, position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    setAdapter(adapter)
    if (position != null && currentItem != position) {
        setCurrentItem(position, false)
    }
}

/**
 * 开启 软键盘
 * 最好需要配合 WindowCompatExt 处理好 window 逻辑后再使用
 * */
@SuppressLint("NewApi")
fun EditText.showIme(window: Window?) {
    // 来源于 WindowInsetsControllerCompat.Impl20.show ime 的方法。
    // 目前 R 以版本 WindowInsetsController.show(ime) 会与导航栏设置颜色冲突，所以都使用 Impl20 的实现

    // We'll try to find an available textView to focus to show the IME
    var targetView: View? = this
    if (isInEditMode || onCheckIsTextEditor()) {
        // The IME needs a text view to be focused to be shown
        // The view given to retrieve this controller is a textView so we can assume
        // that we can focus it in order to show the IME
        requestFocus()
    } else {
        targetView = window?.currentFocus
    }

    // Fallback on the container view
    if (targetView == null) {
        targetView = window?.findViewById(android.R.id.content) ?: return
    }
    if (targetView.hasWindowFocus()) {
        targetView.post {
            val imm = targetView.context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.showSoftInput(targetView, 0)
        }
    } else {
        targetView.viewTreeObserver.addOnWindowFocusChangeListener(object :
            ViewTreeObserver.OnWindowFocusChangeListener {
            override fun onWindowFocusChanged(hasFocus: Boolean) {
                if (targetView.viewTreeObserver.isAlive) {
                    if (hasFocus) {
                        targetView.post {
                            val imm = targetView.context.getSystemService(
                                Context.INPUT_METHOD_SERVICE
                            ) as InputMethodManager
                            imm.showSoftInput(targetView, 0)
                        }
                    }
                    targetView.viewTreeObserver.removeOnWindowFocusChangeListener(this)
                }
            }
        })
    }
}

/**
 * 主动关闭 软键盘
 * 但基本不需要使用。 因为 showIme 已经能处理好 window 关闭后，软键盘的自动关闭。
 * */
@SuppressLint("NewApi")
fun EditText.hideIme(window: Window?) {
    val targetWindowToken = window?.decorView?.windowToken ?: windowToken
    (context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager).hideSoftInputFromWindow(
        targetWindowToken, 0
    )
}

/**
 * 禁止输入空格
 */
fun EditText.disableSpaceInput() {
    filters = arrayOf(object : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val pattern = Regex("[\\s]")
            if (source != null && pattern.containsMatchIn(source)) {
                return ""
            }
            return null
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun View.onScreenTouchListener(onTouch: (Boolean) -> Unit) {
    rootView.setOnTouchListener { v, event ->
        if (event.touchInView(this)) {
            onTouch.invoke(true)
        } else {
            onTouch.invoke(false)
        }
        rootView.setOnTouchListener(null)
        false
    }
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.setDrawableClickListener(drawablePosition: Int, clickListener: (View) -> Unit) {
    val drawable: Drawable? = compoundDrawables[drawablePosition]
    if (drawable != null) {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val x = event.x.toInt()
                val y = event.y.toInt()

                val extraClickArea = 10
                val left = if (drawablePosition == 0) compoundPaddingLeft - extraClickArea else 0
                val right =
                    if (drawablePosition == 2) width - compoundPaddingRight + extraClickArea else width
                val top = if (drawablePosition == 1) compoundPaddingTop - extraClickArea else 0
                val bottom =
                    if (drawablePosition == 3) height - compoundPaddingBottom + extraClickArea else height

                if (x in left..right && y in top..bottom) {
                    clickListener(this)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}


@SuppressLint("ClickableViewAccessibility")
fun TextView.setDrawableClickListener(action: (Int) -> Unit) {
    val extraClickArea = 10
    if (compoundDrawables.filterNotNull().isEmpty()) {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                compoundDrawables.forEachIndexed { drawablePosition, drawable ->
                    if (drawable.bounds.contains(x, y)) {
                        action.invoke(drawablePosition)
                        return@setOnTouchListener true
                    }
                }

            }
            false
        }
    }
}


/**
 * 为TextView的drawable添加点击监听
 * @param extraTouchArea 额外热区，大概返回内都算
 * @param  onDrawableClick lambda表达式，参数为被点击的drawable位置（LEFT=0, TOP=1, RIGHT=2, BOTTOM=3） [android.widget.Drawables]
 */
@SuppressLint("ClickableViewAccessibility")
fun TextView.setOnDrawableClickListener(extraTouchArea :Int= 10, onDrawableClick: (Int) -> Unit) {
    if (compoundDrawables.filterNotNull().isNotEmpty())
        setOnTouchListener(object : View.OnTouchListener{
            var downInDrawables=false
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_DOWN) {
                    val drawables = compoundDrawables
                    drawables.forEachIndexed { index, drawable ->
                        if (drawable != null) {
                            val bounds = drawable.bounds
                            val x = event.x.toInt()
                            val y = event.y.toInt()
                            // 计算drawable的点击区域，每个方向都增加10像素的热区
                            val drawableArea = when (index) {
                                0 -> Rect(
                                    paddingLeft - extraTouchArea,
                                    paddingTop - extraTouchArea,
                                    paddingLeft + drawable.bounds.width() + extraTouchArea,
                                    height - paddingBottom + extraTouchArea
                                ) // LEFT
                                1 -> Rect(
                                    paddingLeft - extraTouchArea,
                                    paddingTop - extraTouchArea,
                                    width - paddingRight + extraTouchArea,
                                    paddingTop + drawable.bounds.height() + extraTouchArea
                                ) // TOP
                                2 -> Rect(
                                    width - paddingRight - drawable.bounds.width() - extraTouchArea,
                                    paddingTop - extraTouchArea,
                                    width - paddingRight + extraTouchArea,
                                    height - paddingBottom + extraTouchArea
                                ) // RIGHT
                                3 -> Rect(
                                    paddingLeft - extraTouchArea,
                                    height - paddingBottom - drawable.bounds.height() - extraTouchArea,
                                    width - paddingRight + extraTouchArea,
                                    height - paddingBottom + extraTouchArea
                                ) // BOTTOM
                                else -> null
                            }

                            // 如果点击在drawable区域内，触发回调
                            if (drawableArea?.contains(x, y) == true) {
                                if (event.action == MotionEvent.ACTION_UP&&downInDrawables) {
                                    onDrawableClick(index)
                                }
                                downInDrawables=event.action == MotionEvent.ACTION_DOWN
                                return true
                            }
                        }
                    }
                }
                return false

            }

        })
}



fun EditText.getSelectionLineBottom(): Int {
    val currentSelectionStart = selectionStart
    val currentLayout: Layout? = layout
    if (currentSelectionStart >= 0 && currentLayout != null) {
        val line = currentLayout.getLineForOffset(currentSelectionStart)
        return currentLayout.getLineBottom(line)
    }
    return -1
}


/**
 * 截图
 */
fun View.screenshot(): Bitmap {
    // 启用DrawingCache
    isDrawingCacheEnabled = true
    // 强制View重新绘制
    buildDrawingCache()
    // 获取DrawingCache
    val bitmap = drawingCache
    // 禁用DrawingCache
    isDrawingCacheEnabled = false
    return bitmap
}

fun View.findViewWithText(ruler:(String)->Boolean): View? {
    if (this is ViewGroup) {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            val foundView =child. findViewWithText( ruler)
            if (foundView != null) {
                return foundView
            }
        }
    }

    // 检查当前View是否是TextView、Button或EditText，并且包含指定文本
    if (isVisible&&this is TextView ) {
        Timber.tag("通通买单").d("通通买单${this.text}")
        if (ruler.invoke(this.text.toString())) {
            return this
        }
    }

    return null
}
