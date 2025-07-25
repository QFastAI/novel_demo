package com.aiso.qfast.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @date：2023/11/27 18:34
 * desc: 添加类的描述
 * author: sunyuxin
 * version：
 **/

/**
 * 设置快速点击按钮多次扩展
 * @param count 设置点击次数触发
 */
fun View.setFastClickCountListener(count: Int = 3, block: () -> Unit) {
    var currentCount = 0
    var lastTime = 0L
    setOnClickListener {
        if (System.currentTimeMillis() - lastTime < 500) {
            currentCount++
        } else {
            currentCount = 1
        }
        lastTime = System.currentTimeMillis()

        if (currentCount >= count) {
            // 重置点击次数
            currentCount = 0
            block.invoke()
        }
    }
}

/**
 * 设置控件点击防抖扩展
 * @param interval 设置防抖时间间隔
 */
fun View.setNoFastClickListener(interval: Int = 600, block: () -> Unit) {
    setOnClickListener {
        val lastClickTime = tag
        val time = (lastClickTime as? Long) ?: 0

        if (System.currentTimeMillis() - time > interval) {
            block.invoke()
            tag = System.currentTimeMillis()
        }
    }
}


/**
 * 设置控件点击防抖扩展
 * @param interval 设置防抖时间间隔
 */
fun View.setNoFastTouchListener(interval: Int = 600, block: () -> Unit) {
    setOnTouchListener {  _, event ->
        val lastClickTime = tag
        val time = (lastClickTime as? Long) ?: 0

        if (System.currentTimeMillis() - time > interval) {
            block.invoke()
            tag = System.currentTimeMillis()
        }
false
    }
}

/**
 * 设置控件点击防抖扩展
 * @param interval 设置防抖时间间隔
 */
fun View.setNoFastClickListenerX(interval: Int = 600, block: (view: View) -> Unit) {
    setOnClickListener {
        val lastClickTime = tag
        val time = (lastClickTime as? Long) ?: 0

        if (System.currentTimeMillis() - time > interval) {
            block.invoke(it)
            tag = System.currentTimeMillis()
        }
    }
}

@SuppressLint("NewApi")
fun View.setOnDoubleClickListener(context: Context, onDoubleClick: () -> Unit) {
    val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleClick()
                return true
            }
        })

    this.setOnTouchListener { _, event ->
        val consumed = gestureDetector.onTouchEvent(event)
        if (!consumed && event.action == MotionEvent.ACTION_UP) {
            performClick()
        }
        consumed
    }
}

@SuppressLint("NewApi")
fun View.setOnMultiClickListener(context: Context, onSingleClick: () -> Unit, onDoubleClick: () -> Unit) {
    val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleClick()
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onSingleClick()
                return true
            }
        })

    this.setOnTouchListener { _, event ->
        val consumed = gestureDetector.onTouchEvent(event)
        if (!consumed && event.action == MotionEvent.ACTION_UP) {
            performClick()
        }
        consumed
    }
}


fun RecyclerView.scrollUnreadMsg(currentPos: Int, totalPos: List<Int>): Int {
    val thisLayoutManager = layoutManager as LinearLayoutManager
    val firstItem = thisLayoutManager.findFirstVisibleItemPosition()

    val currentCanScrolls: List<Int>?
    // 根据长度判断是否是最后一个 是 进来刷一下 当前位置 currentPos
    var endPosition = 0
    if (totalPos.isNotEmpty()) {
        endPosition = totalPos.last()
    }
    currentCanScrolls = if (currentPos == endPosition) {
        totalPos.filter { it >= 0 }
    } else {
        totalPos.filter { it > firstItem }
    }

    return if (currentCanScrolls.isNotEmpty()) {
        val cPos = currentCanScrolls[0]
        moveToPosition(cPos)
        cPos
    } else {
        if (totalPos.isNotEmpty()) {
            val topUnread = totalPos[0]
            moveToPosition(topUnread)
            topUnread
        } else {
            moveToPosition(0)
            0
        }
    }

}

fun RecyclerView.moveToPosition(n: Int) {
    val thisLayoutManager = layoutManager as LinearLayoutManager
    thisLayoutManager.scrollToPositionWithOffset(n, 0)
}
/**
 * 判断点击间隔
 * @param interval 设置防抖时间间隔
 */
fun View.isNoFastCallBack(interval: Int = 600, block: (view: View) -> Unit) {
    val lastClickTime = tag
    val time = (lastClickTime as? Long) ?: 0
    if (System.currentTimeMillis() - time > interval) {
        block.invoke(this)
        tag = System.currentTimeMillis()
    }
}





