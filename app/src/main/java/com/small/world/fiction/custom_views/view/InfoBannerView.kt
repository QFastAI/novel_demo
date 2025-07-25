package com.small.world.fiction.custom_views.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.small.world.fiction.R

class InfoBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    init {
        visibility = INVISIBLE
        setTextColor(Color.WHITE)
        textSize = 14f
        gravity = Gravity.CENTER
        setPadding(24, 12, 24, 12)
        setBackgroundResource(R.drawable.bg_info_banner)
        alpha = 0f
    }

    fun showMessage(message: String, duration: Long = 2000L) {
        text = message
        visibility = VISIBLE
        animate().alpha(1f).setDuration(300).withEndAction {
            postDelayed({
                animate().alpha(0f).setDuration(300).withEndAction {
                    visibility = INVISIBLE
                }
            }, duration)
        }.start()
    }
}
