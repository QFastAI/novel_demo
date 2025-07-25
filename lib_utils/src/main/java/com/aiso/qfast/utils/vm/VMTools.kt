package com.aiso.qfast.utils.vm

import android.annotation.SuppressLint
import android.content.Context

/**
 * 工具初始化入口
 */
@SuppressLint("StaticFieldLeak")
object VMTools {
    private var mContext: Context? = null

    /**
     * 初始化工具类库
     */
    fun init(context: Context) {
        mContext = context
    }

    /**
     * 获取工具类库当前保存的上下文对象:
     * 如果没有进行初始化就从自定义的 VMApp 获取，如果项目也没有继承自 VMApp 则为空
     * 这个主要是为了方便工具类库中的其他接口直接使用上下文对象，不需要在调用相关方法时都传递上下文对象
     */
    val context: Context
        get() {
            return mContext ?: throw NullPointerException("需先调用 VMTools.init()，才能使用 VMTools 工具库")
        }
}