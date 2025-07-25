package com.small.world.fiction.app

import android.app.Application
import com.aiso.qfast.utils.LogUtils
import com.google.firebase.FirebaseApp

class FictionApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        LogUtils.init()
    }
}