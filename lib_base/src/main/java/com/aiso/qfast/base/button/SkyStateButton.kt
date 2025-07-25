package com.aiso.qfast.base.button

import android.content.Context
import android.util.AttributeSet
import com.aiso.qfast.base.R
import androidx.core.content.withStyledAttributes

open class SkyStateButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle
) : SkyButton(context, attrs, defStyleAttr) {

    protected val stateHelper = SkyStateButtonPresenter(this)

    init {
        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.SkyStateButton) {
                stateHelper.loadFromAttributes(this)
            }
        }
        stateHelper.bindStyle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        stateHelper.onLayout(changed, left, top, right, bottom)
    }

    fun bindStyle() {
        stateHelper.bindStyle()
    }

    /**
     *
     * 多使用与夜间模式下，手动更新绑定颜色 night color
     * */
    override fun rebindStyle() {
        super.rebindStyle()
        stateHelper.refreshAttributesColor()
        stateHelper.bindStyle()
    }

    fun changeTextColor(
        normalColor: Int,
        selectColor: Int? = null,
        checkedColor: Int? = null,
        activatedColor: Int? = null,
        pressedColor: Int? = null,
        disableColor: Int? = null
    ) {
        stateHelper.changeTextColor(
            normalColor, selectColor, checkedColor, activatedColor, pressedColor, disableColor
        )
        bindStyle()
    }

    fun changeBackgroundColor(
        normalColor: Int,
        selectColor: Int? = null,
        checkedColor: Int? = null,
        activatedColor: Int? = null,
        pressedColor: Int? = null,
        disableColor: Int? = null
    ) {
        stateHelper.changeBackgroundColor(
            normalColor, selectColor, checkedColor, activatedColor, pressedColor, disableColor
        )
        bindStyle()
    }
}