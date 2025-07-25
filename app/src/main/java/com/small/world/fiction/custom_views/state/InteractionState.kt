package com.small.world.fiction.custom_views.state

enum class InteractionState {
    NORMAL,
    HIGHLIGHTED,   // 闪光状态
    EXPANDED,      // 展开状态
    LOADING,       // 加载中
    SHRUNK,        // 缩略状态
    LOCKED         // 锁定，禁止拖动
}
