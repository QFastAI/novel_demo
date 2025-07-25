package com.small.world.fiction.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    private val horizontalSpace: Int = 0, // 横向间距
    private val verticalSpace: Int = 0    // 纵向间距
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = horizontalSpace / 2
        outRect.right = horizontalSpace / 2
        outRect.top = verticalSpace / 2
        outRect.bottom = verticalSpace / 2
    }
}
