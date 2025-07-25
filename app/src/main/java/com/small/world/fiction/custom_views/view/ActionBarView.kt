package com.small.world.fiction.custom_views.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.small.world.fiction.R

class ActionBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val itemCount = 4
    private var selectedIndex = -1
    private val itemViews = mutableListOf<TextView>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val padding = 12
        setPadding(padding, padding, padding, padding)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        repeat(itemCount) { index ->
            val item = TextView(context).apply {
                text = "条条 $index"
                setTextColor(Color.LTGRAY)
                setBackgroundResource(R.drawable.bg_action_item)
                gravity = Gravity.CENTER
                setPadding(32, 16, 32, 16)
                setOnClickListener { selectItem(index) }
            }
            val lp = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            addView(item, lp)
            itemViews.add(item)
        }
    }

    fun selectItem(index: Int) {
        if (index == selectedIndex) return
        selectedIndex = index

        itemViews.forEachIndexed { i, tv ->
            tv.setTextColor(if (i == index) Color.WHITE else Color.LTGRAY)
            tv.setBackgroundResource(if (i == index) R.drawable.bg_action_item_selected else R.drawable.bg_action_item)
            if (i == index) {
                tv.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction {
                    tv.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }.start()
            }
        }
    }
}
