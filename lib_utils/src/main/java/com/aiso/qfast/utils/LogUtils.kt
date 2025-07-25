package com.aiso.qfast.utils

import android.annotation.SuppressLint
import com.aiso.qfast.base.config.BuildConfig
import timber.log.Timber

@Suppress("KotlinConstantConditions")
class LogUtils {

    companion object {

        private const val TIMBER_TAG = "timerTag:"

        fun init() {
            if(BuildConfig.DEBUG){
                Timber.plant(Timber.DebugTree())
            }
        }

        @SuppressLint("TimberArgCount")
        fun v(content:String, vararg args: Any?, tag:String = TIMBER_TAG){
            Timber.tag(tag).v(content,args)
        }

        @SuppressLint("TimberArgCount")
        fun d(content:String, vararg args: Any?, tag:String = TIMBER_TAG){
            Timber.tag(tag).d(content,args)
        }

        @SuppressLint("TimberArgCount")
        fun i(content:String, vararg args: Any?,tag:String = TIMBER_TAG){
            Timber.tag(tag).i(content,args)
        }

        @SuppressLint("TimberArgCount")
        fun e(content:String, vararg args: Any?,tag:String = TIMBER_TAG){
            Timber.tag(tag).e(content,args)
        }

        @SuppressLint("TimberArgCount")
        fun w(content:String, vararg args: Any?,tag:String = TIMBER_TAG){
            Timber.tag(tag).w(content,args)
        }
    }

}