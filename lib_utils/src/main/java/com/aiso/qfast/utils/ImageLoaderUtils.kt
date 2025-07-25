package com.aiso.qfast.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import androidx.annotation.DrawableRes
import kotlin.let

object ImageLoaderUtils {

    /**
     * 简单加载图片到 ImageView
     */
    fun loadSimple(context: Context, imageView: ImageView, url: String?) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    /**
     * 加载图片时设置占位图和错误图，可传参设置，默认占位图和错误图
     */
    fun loadWithPlaceholder(
        context: Context,
        imageView: ImageView,
        url: String?,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null
    ) {
        val request = Glide.with(context)
            .load(url)
        placeholderRes?.let { request.placeholder(it) }
        errorRes?.let { request.error(it) }
        request.into(imageView)
    }

    /**
     * 简单加载带圆角图片，圆角半径默认16px
     */
    fun loadRoundedSimple(
        context: Context,
        imageView: ImageView,
        url: String?,
        radiusPx: Int = 16
    ) {
        Glide.with(context)
            .load(url)
            .transform(RoundedCorners(radiusPx))
            .into(imageView)
    }

    /**
     *  加载带圆角，支持占位图和错误图参数
     */
    fun loadRoundedWithPlaceholder(
        context: Context,
        imageView: ImageView,
        url: String?,
        radiusPx: Int = 16,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null
    ) {
        val request = Glide.with(context)
            .load(url)
            .transform(RoundedCorners(radiusPx))
        placeholderRes?.let { request.placeholder(it) }
        errorRes?.let { request.error(it) }
        request.into(imageView)
    }

    /**
     * 结合简单圆角加载，增加是否启用缓存及缓存策略控制
     */
    @SuppressLint("CheckResult")
    fun loadRoundedWithCache(
        context: Context,
        imageView: ImageView,
        url: String?,
        radiusPx: Int = 16,
        useCache: Boolean = true,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC
    ) {
        val request = Glide.with(context)
            .load(url)
            .transform(RoundedCorners(radiusPx))
        if (useCache) {
            request.diskCacheStrategy(diskCacheStrategy)
        } else {
            request.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        }
        request.into(imageView)
    }

    /**
     *  结合带占位图圆角加载，增加是否启用缓存及缓存策略控制
     */
    @SuppressLint("CheckResult")
    fun loadRoundedWithPlaceholderAndCache(
        context: Context,
        imageView: ImageView,
        url: String?,
        radiusPx: Int = 16,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null,
        useCache: Boolean = true,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC
    ) {
        val request = Glide.with(context)
            .load(url)
            .transform(RoundedCorners(radiusPx))
        placeholderRes?.let { request.placeholder(it) }
        errorRes?.let { request.error(it) }
        if (useCache) {
            request.diskCacheStrategy(diskCacheStrategy)
        } else {
            request.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        }
        request.into(imageView)
    }

    /**
     * 带缓存设置和加载监听的简单圆角加载
     */
    @SuppressLint("CheckResult")
    fun loadRoundedWithCacheAndListener(
        context: Context,
        imageView: ImageView,
        url: String?,
        radiusPx: Int = 16,
        useCache: Boolean = true,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
        listener: RequestListener<Drawable>? = null
    ) {
        val request = Glide.with(context)
            .load(url)
            .transform(RoundedCorners(radiusPx))
        if (useCache) {
            request.diskCacheStrategy(diskCacheStrategy)
        } else {
            request.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        }
        listener?.let { request.listener(it) }
        request.into(imageView)
    }

    /**
     * 带缓存设置、占位图和加载监听的圆角加载
     */
    @SuppressLint("CheckResult")
    fun loadRoundedWithPlaceholderCacheAndListener(
        context: Context,
        imageView: ImageView,
        url: String?,
        radiusPx: Int = 16,
        @DrawableRes placeholderRes: Int? = null,
        @DrawableRes errorRes: Int? = null,
        useCache: Boolean = true,
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
        listener: RequestListener<Drawable>? = null
    ) {
        val request = Glide.with(context)
            .load(url)
            .transform(RoundedCorners(radiusPx))
        placeholderRes?.let { request.placeholder(it) }
        errorRes?.let { request.error(it) }
        if (useCache) {
            request.diskCacheStrategy(diskCacheStrategy)
        } else {
            request.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        }
        listener?.let { request.listener(it) }
        request.into(imageView)
    }
}
