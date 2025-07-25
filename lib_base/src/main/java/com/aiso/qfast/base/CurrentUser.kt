package com.aiso.qfast.base

import com.aiso.qfast.base.config.BuildConfig

object CurrentUser {
    fun isLogin(): Boolean{
        return BuildConfig.currentUserId.isNotBlank()
    }
}