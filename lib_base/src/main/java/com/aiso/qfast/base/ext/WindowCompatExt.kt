@file:Suppress("DEPRECATION")

package com.aiso.qfast.base.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aiso.qfast.base.ext.dp
import com.google.android.material.color.MaterialColors

/**
 * 使用 Androidx 方案处理显示状态栏和导航栏
 * 全屏 + 显示 system bars + 控制 system bars 颜色
 */
@SuppressLint("NewApi")
fun Window?.showSystemBars(
    @ColorInt statusBarColor: Int = Color.TRANSPARENT,
    @ColorInt navigationBarColor: Int = Color.TRANSPARENT,
    isAppearanceLightBars: Boolean = false,
    isContrastEnforcedBars: Boolean = false
) {
    if (this == null) {
        return
    }
    this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)
    WindowCompat.setDecorFitsSystemWindows(this, false)
    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        this.attributes = this.attributes.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    // 小米 Android 11 莫名其妙的 bug ，设置了 navigationBarColor 后，会影响状态栏的 isAppearanceLightStatusBars。
    // 所以先配置 NavigationBar，再配置 StatusBars
    if (isAppearanceLightBars) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.navigationBarColor = navigationBarColor
        } else {
            this.navigationBarColor = 0x80000000.toInt()
        }
        windowInsetsController.isAppearanceLightNavigationBars = true
    } else {
        this.navigationBarColor = navigationBarColor
        windowInsetsController.isAppearanceLightNavigationBars = false
        //  Android 11 isAppearanceLightXXX 是恢复到主题默认。 但这里期望是设置黑底白字。需要在处理一下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

    if (isAppearanceLightBars) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.statusBarColor = statusBarColor
        } else {
            this.statusBarColor = 0x80000000.toInt()
        }
        windowInsetsController.isAppearanceLightStatusBars = true
    } else {
        this.statusBarColor = statusBarColor
        windowInsetsController.isAppearanceLightStatusBars = false
        //  Android 11 isAppearanceLightXXX 是恢复到主题默认。 但这里期望是设置黑底白字。需要在处理一下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.isStatusBarContrastEnforced = isContrastEnforcedBars
        this.isNavigationBarContrastEnforced = isContrastEnforcedBars
    }
}

/**
 * 使用 Androidx 方案处理隐藏状态栏和导航栏
 * @param type 默认只隐藏 statusBars
 * @param systemBarsBehavior ，默认是 BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE （沉浸式）。使用时需要在 onWindowFocusChanged hasFocus 是再调此方法
 * */
@SuppressLint("NewApi")
fun Window?.hideSystemBars(
    type: Int = WindowInsetsCompat.Type.statusBars(),
    systemBarsBehavior: Int = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
) {
    if (this == null) {
        return
    }
    val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)
    this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    WindowCompat.setDecorFitsSystemWindows(this, false)
    windowInsetsController.hide(type)
    windowInsetsController.systemBarsBehavior = systemBarsBehavior

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        this.attributes = this.attributes.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    this.navigationBarColor = Color.TRANSPARENT
    windowInsetsController.isAppearanceLightNavigationBars = false
    this.statusBarColor = Color.TRANSPARENT
    windowInsetsController.isAppearanceLightStatusBars = false

    //  Android 11 isAppearanceLightXXX 是恢复到主题默认。 但这里期望是设置黑底白字。需要在处理一下
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        decorView.systemUiVisibility =
            decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.isStatusBarContrastEnforced = false
        this.isNavigationBarContrastEnforced = false
    }
}

/**
 *  使用 Androidx 方案处理 状态栏颜色
 */
@SuppressLint("NewApi")
fun Window?.statusBarColor(
    @ColorInt color: Int, isAppearanceLightBars: Boolean = false
) {
    if (this == null) {
        return
    }
    val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)

    if (isAppearanceLightBars) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.statusBarColor = color
        } else {
            this.statusBarColor = 0x80000000.toInt()
        }
        windowInsetsController.isAppearanceLightStatusBars = true
    } else {
        this.statusBarColor = color
        windowInsetsController.isAppearanceLightStatusBars = false
        //  Android 11 isAppearanceLightXXX 是恢复到主题默认。 但这里期望是设置黑底白字。需要在处理一下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

/**
 *  使用 Androidx 方案处理 导航栏颜色
 */
@SuppressLint("NewApi")
fun Window?.navigationBarColor(
    @ColorInt color: Int, isAppearanceLightBars: Boolean = false
) {
    if (this == null) {
        return
    }
    val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)

    // 小米 Android 11 莫名其妙的 bug ，设置了 navigationBarColor 后，会影响状态栏的 isAppearanceLightStatusBars。
    // 需要再恢复一下
    val lastStatusBarsAppearance = windowInsetsController.isAppearanceLightStatusBars

    if (isAppearanceLightBars) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.navigationBarColor = color
        } else {
            this.navigationBarColor = 0x80000000.toInt()
        }
        windowInsetsController.isAppearanceLightNavigationBars = true
    } else {
        this.navigationBarColor = color
        windowInsetsController.isAppearanceLightNavigationBars = false
        //  Android 11 isAppearanceLightXXX 是恢复到主题默认。 但这里期望是设置黑底白字。需要在处理一下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

    // 这里特殊处理 小米 Android 11
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowInsetsController.isAppearanceLightStatusBars = lastStatusBarsAppearance
    }
}

private val WINDOW_INSET_BOTTOM_MIN_HEIGHT = 30.dp()

fun Window?.navigationBarAutoEnforced(
    windowInsets: WindowInsetsCompat,
    @ColorRes colorRes: Int = ResourcesCompat.ID_NULL,
    isAppearanceLightBars: Boolean = false
) {
    if (this == null) {
        return
    }
    val navigationBarsHeight = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
    val navigationBarColor =
        if (navigationBarsHeight > WINDOW_INSET_BOTTOM_MIN_HEIGHT && colorRes != ResourcesCompat.ID_NULL) {
            ContextCompat.getColor(context, colorRes)
        } else {
            Color.TRANSPARENT
        }
    navigationBarColor(navigationBarColor, isAppearanceLightBars)
}

fun Window?.navigationBarAutoStyle(
    windowInsets: WindowInsetsCompat,
    isAppearanceLightBars: Boolean = false,
    @FloatRange(from = 0.00, to = 1.00) alpha: Float = 0.5f
) {
    if (this == null) {
        return
    }
    val navigationBarsHeight = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
    val navigationBarColor =
        if (navigationBarsHeight > WINDOW_INSET_BOTTOM_MIN_HEIGHT && alpha > 0) {
            ColorUtils.setAlphaComponent(
                MaterialColors.getColor(
                    context, android.R.attr.colorBackground, Color.TRANSPARENT
                ), (255 * alpha).toInt()
            )
        } else {
            Color.TRANSPARENT
        }
    navigationBarColor(navigationBarColor, isAppearanceLightBars)
}

@SuppressLint("NewApi")
fun Window?.hideIme() {
    if (this == null) {
        return
    }
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        decorView.windowToken, 0
    )
}






