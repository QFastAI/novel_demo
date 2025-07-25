package com.aiso.qfast.base.refresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.aiso.qfast.base.databinding.LayoutRefreshFooterBinding
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

@SuppressLint("NewApi")
class QFastRefreshFooter :FrameLayout, RefreshFooter {
    private lateinit var binding: LayoutRefreshFooterBinding
    private var mNoMoreData: Boolean = false

    companion object {
        const val REFRESH_FOOTER_PULLING: String = "上拉加载更多" //"上拉加载更多";
        const val REFRESH_FOOTER_RELEASE: String = "释放立即加载" //"释放立即加载";
        const val REFRESH_FOOTER_LOADING: String = "加载中" //"正在加载...";
        const val REFRESH_FOOTER_FINISH: String = "加载完成" //"加载完成";
        const val REFRESH_FOOTER_FAILED: String = "加载失败" //"加载失败";
        const val REFRESH_FOOTER_NOTHING: String = "没有更多数据了" //"没有更多数据了";
    }

    init {
        if (!isInEditMode) {
            binding = LayoutRefreshFooterBinding.inflate(
                LayoutInflater.from(context), this, true
            )

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
        if (!mNoMoreData) {
            when (newState) {
                RefreshState.None -> {
                    binding.refreshTextView.isVisible = true
                    binding.refreshTextView.text = REFRESH_FOOTER_PULLING
                }

                RefreshState.PullUpToLoad -> {
                    binding.refreshTextView.isVisible = true
                    binding.refreshTextView.text = REFRESH_FOOTER_PULLING
                }

                RefreshState.ReleaseToLoad -> {
                    binding.refreshTextView.isVisible = true
                    binding.refreshTextView.text = REFRESH_FOOTER_RELEASE
                }

                RefreshState.Loading, RefreshState.LoadReleased -> {
                    binding.refreshTextView.isVisible = true
                    binding.refreshTextView.text = REFRESH_FOOTER_LOADING
                }
                RefreshState.Refreshing->{
                    binding.refreshTextView.isVisible = true
                    binding.refreshTextView.text = REFRESH_FOOTER_LOADING
                }
                else -> {
                    binding.refreshTextView.isVisible = true
                    binding.refreshTextView.text = ""
                }
            }
        }else{
            binding.refreshTextView.isVisible = true
            binding.refreshTextView.text = REFRESH_FOOTER_NOTHING
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
        if (!mNoMoreData) {
            binding.refreshTextView.isVisible = true
            if (success) {
                binding.refreshTextView.text = REFRESH_FOOTER_FINISH
            } else {
                binding.refreshTextView.text = REFRESH_FOOTER_FAILED
            }
            return 500
        }else {
            binding.refreshTextView.isVisible = true
            binding.refreshTextView.text = REFRESH_FOOTER_NOTHING
        }
        return 0
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

    @SuppressLint("RestrictedApi")
    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        binding.refreshTextView.text = REFRESH_FOOTER_NOTHING
        return true
    }
}