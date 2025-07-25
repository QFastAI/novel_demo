package com.aiso.qfast.base.ext

import com.gyf.immersionbar.ImmersionBar

fun ImmersionBar.hideStatusBarAndNavigationBar(fitsSystemWindows:Boolean = false){
    this.statusBarColor(android.R.color.transparent)
        .fitsSystemWindows(fitsSystemWindows)
        .statusBarColorTransform(android.R.color.transparent)
        .navigationBarColorTransform(android.R.color.transparent)
        .fullScreen(true)
        .barAlpha(1F)
}