package com.aiso.qfast.base.refresh

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.aiso.qfast.base.databinding.LayoutRefreshHeaderBinding
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

@SuppressLint("NewApi")
class QFastRefreshHeader : FrameLayout, RefreshHeader {
    private lateinit var binding: LayoutRefreshHeaderBinding

    companion object {
        const val REFRESH_HEADER_PULLING: String = "下拉刷新"
        const val REFRESH_HEADER_RELEASE: String = "释放刷新"
        const val REFRESH_HEADER_FINISH: String = "刷新完成"
        const val REFRESH_HEADER_FAILED: String = "刷新失败"
    }

    init {
        if (!isInEditMode) {
            binding = LayoutRefreshHeaderBinding.inflate(
                LayoutInflater.from(context), this, true
            )
//            binding.pagView.load(LoadingResource.GRADIENT)
//            binding.pagView.isInvisible = true
        }
    }

    constructor(context: Context) : super(context) {
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState,
    ) {
        when (newState) {
            RefreshState.None -> {
                binding.refreshTextView.isVisible = true
                binding.loadingImage.isVisible = false
                binding.refreshTextView.text = REFRESH_HEADER_PULLING
            }

            RefreshState.PullDownToRefresh -> {
                binding.refreshTextView.isVisible = true
                binding.loadingImage.isVisible = false
                binding.refreshTextView.text = REFRESH_HEADER_PULLING
            }

            RefreshState.ReleaseToRefresh -> {
                binding.refreshTextView.isVisible = true
                binding.loadingImage.isVisible = false
                binding.refreshTextView.text = REFRESH_HEADER_RELEASE
            }

            RefreshState.RefreshReleased, RefreshState.Refreshing -> {
                binding.refreshTextView.isVisible = false
                binding.loadingImage.isVisible = true
                val animator = ObjectAnimator.ofFloat(binding.loadingImage, "rotationY", 0f, 360f)
                animator.duration = 1000L            // 动画时长 1 秒
                animator.repeatCount = ValueAnimator.INFINITE // 无限循环
                animator.interpolator = LinearInterpolator()  // 匀速
                animator.start()
                binding.refreshTextView.text = ""
            }

            else -> {
                binding.refreshTextView.isVisible = true
                binding.loadingImage.isVisible = false
                binding.refreshTextView.text = ""
            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate;
    }

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg colors: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        //this.kernel = kernel
        //
        //kernel.requestDrawBackgroundFor(
        //    this,
        //    if (mDesColor !== 0) mDesColor else Color.parseColor("#ffffff")
        //)
    }

    @SuppressLint("RestrictedApi")
    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int,
    ) {

    }

    @SuppressLint("RestrictedApi")
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        binding.refreshTextView.isVisible = true
        if (success) {
            binding.refreshTextView.text = REFRESH_HEADER_FINISH
        } else {
            binding.refreshTextView.text = REFRESH_HEADER_FAILED
        }
        return 500
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false;
    }

    override fun autoOpen(duration: Int, dragRate: Float, animationOnly: Boolean): Boolean {
        return false
    }

    val Int.dp: Float get() = this.toFloat().dp
    val Int.dpi: Int get() = this.toFloat().dpi

    val Float.dp: Float
        get() {
            val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displayMetrics)
        }
    val Float.dpi: Int
        get() {
            val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
            return (this * displayMetrics.density).toInt()
        }
}