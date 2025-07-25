package com.aiso.qfast.base.ext
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max

open class IMEWindowInsetsAnimationListener :
    WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    OnApplyWindowInsetsListener {

    private var _view: View? = null
    private var _lastWindowInsets: WindowInsetsCompat? = null
    private var _isRunning = false
    private var _isImeVisible = false
    private var _lastDiffHeight: Int? = null

    final override fun onApplyWindowInsets(
        view: View, insets: WindowInsetsCompat
    ): WindowInsetsCompat {
        _view = view
        _lastWindowInsets = insets

        val imeInset = insets.getInsets(WindowInsetsCompat.Type.ime())
        val systemBarsInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        _isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

        if (_isRunning) {
            onAnimationPrepare(insets, _isImeVisible, max(imeInset.bottom, systemBarsInset.bottom))
            return WindowInsetsCompat.CONSUMED
        } else {
            onWindowInsetsListener(
                insets,
                _isImeVisible,
                max(imeInset.bottom, systemBarsInset.bottom)
            )
            return insets
        }
    }

    final override fun onStart(
        animation: WindowInsetsAnimationCompat, bounds: WindowInsetsAnimationCompat.BoundsCompat
    ): WindowInsetsAnimationCompat.BoundsCompat {
        return super.onStart(animation, bounds)
    }

    final override fun onPrepare(animation: WindowInsetsAnimationCompat) {
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            _isRunning = true
            _lastDiffHeight = null
        }
    }

    final override fun onEnd(animation: WindowInsetsAnimationCompat) {
        if (_isRunning && animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            _isRunning = false
            _lastDiffHeight = null
            onAnimationEnd()
        }
        val view = _view
        val lastWindowInsets = _lastWindowInsets
        if (view != null && lastWindowInsets != null) {
            ViewCompat.dispatchApplyWindowInsets(view, lastWindowInsets)
        }
    }

    final override fun onProgress(
        insets: WindowInsetsCompat, runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        if (_isRunning) {
            val imeInset = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val animationMaxInsetBottom = max(imeInset.bottom, systemBarsInset.bottom)
            val diffInset = Insets.subtract(imeInset, systemBarsInset)
                .let { Insets.max(it, Insets.NONE) }

            val animationHeight = diffInset.bottom - diffInset.top

            val lastDiffHeight = _lastDiffHeight
            val diffY = if (lastDiffHeight != null) {
                lastDiffHeight - animationHeight
            } else {
                0
            }
            _lastDiffHeight = animationHeight
            onAnimationProgress(
                _isImeVisible, animationMaxInsetBottom, animationHeight, diffY,
            )
        }
        return insets
    }

    open fun onWindowInsetsListener(
        windowInsets: WindowInsetsCompat, isImeVisible: Boolean, maxInsetBottom: Int
    ) {
    }

    open fun onAnimationPrepare(
        windowInsets: WindowInsetsCompat, isImeVisible: Boolean, maxInsetBottom: Int
    ) {
    }

    open fun onAnimationProgress(
        isImeVisible: Boolean, animationMaxInsetBottom: Int, animationHeight: Int, diffY: Int
    ) {
    }

    open fun onAnimationEnd() {}
}