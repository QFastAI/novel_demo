package com.aiso.qfast.base.button

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import com.aiso.qfast.base.R
import androidx.core.content.withStyledAttributes

open class SkyButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val buttonPresenter = SkyButtonPresenter(this)

    init {
        // this.setSupportAllCaps(false)

        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.SkyButton) {
                buttonPresenter.loadFromAttributes(this)
            }
        }
        this.includeFontPadding = buttonPresenter.includeFontPadding
        this.gravity = if (buttonPresenter.textAutoCenter) {
            Gravity.CENTER
        } else {
            Gravity.CENTER_VERTICAL
        }
        buttonPresenter.updateIcon()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        buttonPresenter.measure()
    }

    @JvmOverloads
    fun setIcon1(
        @DrawableRes resId: Int,
        width: Int = 0,
        height: Int = 0,
        tint: ColorStateList? = null,
        @SkyButtonPresenter.Companion.IconGravity gravity: Int? = null
    ) {
        buttonPresenter.setIcon1(
            buttonPresenter.getResIdDrawable(resId), width, height, tint, gravity
        )
    }

    @JvmOverloads
    fun setIcon1(
        drawable: Drawable? = null,
        width: Int = 0,
        height: Int = 0,
        tint: ColorStateList? = null,
        @SkyButtonPresenter.Companion.IconGravity gravity: Int? = null
    ) {
        buttonPresenter.setIcon1(drawable, width, height, tint, gravity)
    }

    @JvmOverloads
    fun setIcon2(
        @DrawableRes resId: Int,
        width: Int = 0,
        height: Int = 0,
        tint: ColorStateList? = null,
        @SkyButtonPresenter.Companion.IconGravity gravity: Int? = null
    ) {
        buttonPresenter.setIcon2(
            buttonPresenter.getResIdDrawable(resId), width, height, tint, gravity
        )
    }

    @JvmOverloads
    fun setIcon2(
        drawable: Drawable? = null,
        width: Int = 0,
        height: Int = 0,
        tint: ColorStateList? = null,
        @SkyButtonPresenter.Companion.IconGravity gravity: Int? = null
    ) {
        buttonPresenter.setIcon2(drawable, width, height, tint, gravity)
    }

    open fun rebindStyle() {
        buttonPresenter.rebindStyle()
    }
}
